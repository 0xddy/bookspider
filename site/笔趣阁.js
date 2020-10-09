/**
 * 必有的字段参数
 * @type {{name: string, version: number}}
 */
var project = {
    id: '10001',
    name: '笔趣阁采集规则',
    author: '桥下红药',
    version: "1.0",
    baseUrl: 'http://www.biquge.info'
};

var category = [
    {title: '玄幻小说', id: 1, page: {start: 1, end: 672}},
    {title: '修真小说', id: 2, page: {start: 1, end: 278}},
    {title: '都市小说', id: 3, page: {start: 1, end: 900}},
    {title: '穿越小说', id: 4, page: {start: 1, end: 203}},
    {title: '网游小说', id: 5, page: {start: 1, end: 225}},
    {title: '科幻小说', id: 6, page: {start: 1, end: 808}}
];

var filter = ['【(.*?)】'];

function getBaseUrl() {
    return project.baseUrl;
}

function getCategory() {
    return category;
}

/**
 * 获取翻页url
 * @param cateId    分类ID
 * @param currentPage   当前页码
 * @returns {string}
 */
function getPageUrl(cateId, currentPage) {
    return getBaseUrl() + '/list/' + cateId + '_' + currentPage + '.html';
}

/**
 * 解析dom中的小说链接
 * @param html
 */
function getBooks(html) {
    var urls = [];
    var $ = native_jparse.parse(html);
    var liArray = $.select("#newscontent > div.l > ul > li");
    for (var i = 0; i < liArray.length; i++) {
        var a = liArray[i].select("span.s2 > a");
        var name = a.text();
        var url = a.attr('href');
        var author = liArray[i].select("span.s5").text();
        urls.push({name: name, url: url, author: author, status: 0});
    }
    return urls;
}

/**
 * 解析小说详情页
 * @param html
 * @returns {{img: *, chapters: [], author: *, intro: *, name: (*|Promise<string>|string), lastime: *}}
 */
function getBookDetail(html) {
    //获取基本信息
    var $ = native_jparse.parse(html);
    var name = $.select("#info > h1").text();
    var author = $.select("#info > p:nth-child(2)").text().replaceAll('作(.*)者：', '');
    var lastime = $.select("#info > p:nth-child(4)").text().replaceAll('最后更新(.*)：', '')
    var img = $.select("#fmimg > img").attr('src');
    var intro = $.select("#intro > p:nth-child(2)").text();
    //获取章节
    var chapters = [];
    var aArray = $.select("#list > dl > dd > a");
    for (var i = 0; i < aArray.length; i++) {
        chapters.push({title: aArray[i].text(), url: aArray[i].attr('href')});
    }

    return {name: name, author: author, img: img, intro: filterStr(intro), lastime: lastime, chapters: chapters};
}

/**
 * 读取章节内容
 * @param html
 * @returns {{content: *}}
 */
function getChapterContent(html) {
    var $ = native_jparse.parse(html);
    $.select('#content > p').remove();
    var content = $.select('#content').html();
    return {content: content};
}


/**
 * 关键字替换
 * @param str
 * @returns {*}
 */
function filterStr(str) {
    for (var i = 0; i < filter.length; i++) {
        str = str.replaceAll(filter[i], '');
    }
    return str;
}


