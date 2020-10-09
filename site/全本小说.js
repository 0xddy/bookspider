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
    {title: '玄幻小说', id: 1, page: {start: 1, end: 1429}},
    {title: '修真小说', id: 2, page: {start: 1, end: 555}},
    {title: '言情小说', id: 3, page: {start: 1, end: 852}},
    {title: '历史小说', id: 4, page: {start: 1, end: 260}},
    {title: '网游小说', id: 5, page: {start: 1, end: 233}},
    {title: '科幻小说', id: 6, page: {start: 1, end: 608}},
    {title: '官场小说', id: 7, page: {start: 1, end: 339}},
    {title: '穿越小说', id: 8, page: {start: 1, end: 519}},
    {title: '其他小说', id: 9, page: {start: 1, end: 25}}
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
    var liArray = $.select("#content > div > div.details.list-type > ul > li");
    for (var i = 0; i < liArray.length; i++) {
        var a = liArray[i].select("span.s2 > a");
        var name = a.text();
        var url = a.attr('href');
        var author = liArray[i].select("span.s3").text();
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


