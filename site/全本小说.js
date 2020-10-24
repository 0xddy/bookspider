/**
 * 必有的字段参数
 * @type {{name: string, version: number}}
 */
var project = {
    id: '10002',
    name: '全本小说网采集规则',
    author: '桥下红药',
    version: "1.0",
    baseUrl: 'https://www.quanben.net'
};

var category = [
    {title: '玄幻小说', id: 1, page: {start: 1, end: 1429}, posto: 1},
    {title: '修真小说', id: 2, page: {start: 1, end: 555}, posto: 2},
    {title: '言情小说', id: 3, page: {start: 1, end: 852}, posto: 3},
    {title: '历史小说', id: 4, page: {start: 1, end: 260}, posto: 4},
    {title: '网游小说', id: 5, page: {start: 1, end: 233}, posto: 5},
    {title: '科幻小说', id: 6, page: {start: 1, end: 608}, posto: 6},
    {title: '官场小说', id: 7, page: {start: 1, end: 339}, posto: 9},
    {title: '穿越小说', id: 8, page: {start: 1, end: 519}, posto: 7},
    {title: '其他小说', id: 9, page: {start: 1, end: 25}, posto: 8}
];

var filter = ['【(.*?)】', '全本书-免费全本小说阅读网'];

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
    var liArray = $.select("#content > div > div.details.list-type > ul > li");
    for (var i = 0; i < liArray.length; i++) {
        var a = liArray[i].select("span.s2 > a");
        var name = a.text();
        var url = a.attr('href');
        var author = liArray[i].select("span.s3").text().trim();
        var status = 0;
        var statusStr = liArray[i].select("span.s5").text();
        if (statusStr === '连载') {
            status = 1;
        } else if (statusStr === '完结') {
            status = 2;
        }
        urls.push({name: name, url: getBaseUrl() + url, author: author, status: status});
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
    var url = $.select("link[rel='alternate']").attr('href');
    var name = $.select("#container > div.bookinfo > div > h1").text();
    var author = $.select("#container > div.bookinfo > div > em > a").text()
        .replaceAll('作(.*)者：', '').trim();

    var lastime = $.select("#container > div.bookinfo > p.stats > span.fr > i").text();
    if (lastime !== '') {
        lastime = Date.parse(lastime) / 1000;
    }

    var regs = url.match(/(\d+)\/(\d+)/);
    var img = getBaseUrl() + '/files/article/image/' + regs[1] + '/' + regs[2] + '/' + regs[2] + 's.jpg'
    var intro = $.select("#container > div.bookinfo > p.intro").text()
        .replaceAll('内容简介：', '');

    //获取章节
    var chapters = [];
    var aArray = $.select("#main > div > dl dt").last().nextElementSiblings().select('a');
    for (var i = 0; i < aArray.length; i++) {
        chapters.push({title: aArray[i].text(), url: getBaseUrl() + aArray[i].attr('href')});
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
    $.select('#BookText > a').remove();
    var title = $.select('#BookCon > h1').text();
    var content = $.select('#BookText').html();
    return {title: title, content: filterStr(content)};
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


