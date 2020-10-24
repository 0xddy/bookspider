package cn.lmcw.bookspider.parse;

import cn.lmcw.bookspider.model.*;
import com.google.gson.GsonBuilder;
import jdk.nashorn.api.scripting.NashornScriptEngine;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.apache.commons.io.FileUtils;

import javax.script.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EngineBridge implements IBridge {

    private final NashornScriptEngine scriptEngine;

    {
        ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
        scriptEngine = (NashornScriptEngine) scriptEngineManager.getEngineByName("nashorn");
        ScriptContext newContext = new SimpleScriptContext();
        newContext.setBindings(scriptEngine.createBindings(), ScriptContext.ENGINE_SCOPE);

        scriptEngine.put("native_jparse", JParse.getInstance());
        scriptEngine.put("native_bridge", new NativeBridge());
    }

    public EngineBridge(String uri) throws Exception {
        reload(uri);
    }

    private File jsFile = null;

    public void reload(String uri) throws Exception {
        jsFile = new File(uri);
        if (!jsFile.exists() || !jsFile.canRead()) {
            throw new FileNotFoundException();
        }
        String jsCodeStr = FileUtils.readFileToString(jsFile, "UTF-8");
        CompiledScript compiledScript = scriptEngine.compile(jsCodeStr);
        compiledScript.eval();
    }


    @Override
    public String getBaseUrl() {
        return invokeFunctionToString("getBaseUrl");
    }

    @Override
    public List<Category> getCategory() throws ScriptException, NoSuchMethodException {
        List<Category> categories;
        ScriptObjectMirror scriptObjectMirror = (ScriptObjectMirror) scriptEngine
                .invokeFunction("getCategory");
        categories = scriptObjectMirror.values().stream().flatMap(new Function<Object, Stream<Category>>() {
            ScriptObjectMirror map;
            ScriptObjectMirror page;

            @Override
            public Stream<Category> apply(Object o) {
                map = ((ScriptObjectMirror) o);
                page = (ScriptObjectMirror) map.get("page");
                Category category = new Category();
                category.setId((int) map.get("id"));
                category.setTitle((String) map.get("title"));
                category.setPosto((int) map.get("posto"));

                Category.PageDTO pageDTO = new Category.PageDTO();
                pageDTO.setStart((int) page.get("start"));
                pageDTO.setEnd((int) page.get("end"));
                category.setPage(pageDTO);

                map = page = null;
                return Stream.of(category);
            }
        }).collect(Collectors.toList());

        return categories;
    }

    @Override
    public String getPageUrl(int cateId, int currentPage) {
        return invokeFunctionToString("getPageUrl", cateId, currentPage);
    }

    @Override
    public List<BookHref> getBooks(String html) throws ScriptException, NoSuchMethodException {

        ScriptObjectMirror scriptObjectMirror = (ScriptObjectMirror) scriptEngine.invokeFunction("getBooks", html);

        return scriptObjectMirror.values().stream().flatMap(new Function<Object, Stream<BookHref>>() {
            ScriptObjectMirror map;

            @Override
            public Stream<BookHref> apply(Object o) {
                map = (ScriptObjectMirror) o;
                BookHref bookHref = new BookHref((String) map.get("name"), (String) map.get("url"),
                        (String) map.get("author"), (int) map.get("status"));
                return Stream.of(bookHref);
            }
        }).collect(Collectors.toList());
    }

    @Override
    public Book getBookDetail(String html) throws ScriptException, NoSuchMethodException {

        ScriptObjectMirror scriptObjectMirror = (ScriptObjectMirror) scriptEngine.invokeFunction("getBookDetail", html);
        String name = (String) scriptObjectMirror.get("name");
        String author = (String) scriptObjectMirror.get("author");
        double lastime = (double) scriptObjectMirror.get("lastime");
        String img = (String) scriptObjectMirror.get("img");
        String intro = (String) scriptObjectMirror.get("intro");

        Book book = new Book();
        book.setName(name);
        book.setAuthor(author);
        book.setImg(img);
        book.setIntro(intro);
        book.setLastime((int) lastime);

        if (scriptObjectMirror.containsKey("chapters")) {
            List<Book.Chapter> chapters = ((ScriptObjectMirror) scriptObjectMirror.get("chapters")).values()
                    .stream().flatMap(new Function<Object, Stream<Book.Chapter>>() {
                        ScriptObjectMirror map;
                        Book.Chapter chapter;

                        @Override
                        public Stream<Book.Chapter> apply(Object o) {
                            map = (ScriptObjectMirror) o;
                            chapter = new Book.Chapter();
                            chapter.setTitle((String) map.get("title"));
                            chapter.setUrl((String) map.get("url"));
                            return Stream.of(chapter);
                        }
                    }).collect(Collectors.toList());
            book.setChapters(chapters);
        }

        return book;
    }

    @Override
    public ChapterContent getChapterContent(String html) throws ScriptException, NoSuchMethodException {
        ScriptObjectMirror chapterObject = (ScriptObjectMirror) scriptEngine
                .invokeFunction("getChapterContent", html);

        ChapterContent chapterContent = new ChapterContent();
        chapterContent.setTitle((String) chapterObject.get("title"));
        chapterContent.setContent((String) chapterObject.get("content"));

        return chapterContent;
    }

    @Override
    public Project project() {
        ScriptObjectMirror projectObject = (ScriptObjectMirror) scriptEngine.get("project");
        Project projectModel = new Project();
        projectModel.setId((String) projectObject.get("id"));
        projectModel.setName((String) projectObject.get("name"));
        projectModel.setAuthor((String) projectObject.get("author"));
        projectModel.setVersion((String) projectObject.get("version"));
        projectModel.setBaseUrl((String) projectObject.get("baseUrl"));

        projectModel.setFileName(jsFile == null ? "Null" : jsFile.getName());
        return projectModel;
    }

    @Override
    public String getCharset() {
        String htmlCharset = "utf-8";
        try {
            htmlCharset = (String) scriptEngine.get("charset");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return htmlCharset;
    }

    public int getCategoryMappingId(String categoryName) {
        int mappingId = 0;
        try {

            List<Category> categories = getCategory();
            for (Category category : categories) {
                if (category.getTitle().equals(categoryName)) {
                    mappingId = category.getPosto();
                    break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return mappingId;
    }

    private String invokeFunctionToString(String funcName, Object... args) {
        try {
            Object retVal = scriptEngine.invokeFunction(funcName, args);
            if (retVal instanceof ScriptObjectMirror) {
                ScriptObjectMirror objectMirror = (ScriptObjectMirror) retVal;
                if (objectMirror.isArray()) {
                    return new GsonBuilder().disableHtmlEscaping().create().toJson(objectMirror.values());
                }
                if (objectMirror.isExtensible()) {
                    return new GsonBuilder().disableHtmlEscaping().create().toJson(objectMirror);
                } else {
                    System.out.println("未定义处理类型");
                }
            } else if (retVal instanceof Integer) {
                return String.valueOf((int) retVal);
            } else if (retVal instanceof Double) {
                return String.valueOf((double) retVal);
            } else if (retVal instanceof Float) {
                return String.valueOf((float) retVal);
            } else if (retVal instanceof Long) {
                return String.valueOf((long) retVal);
            } else if (retVal instanceof String) {
                return (String) retVal;
            }
            System.out.println("未定义处理类型");
            return null;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}
