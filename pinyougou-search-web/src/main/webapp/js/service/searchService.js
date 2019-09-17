app.service('searchService',function ($http) {
    //搜索方法
    this.search=function (searchMap) {
        return $http.post('itemSearch/search.do',searchMap);
    }
})