app.controller('searchController',function ($scope, searchService) {
    $scope.searchMap={'keywords':'','category':'','brand':'','spec':{}};//搜索对象
    //搜索
    $scope.search=function () {
        searchService.search($scope.searchMap).success(
            function (response) {
                $scope.resultMap=response;
            });

    }
    //添加搜索项 改变searchMao值
    $scope.addSearchItem=function (key,value) {
        //如果点击的是分类或者是品牌
        if (key == 'category' || key == 'brand') {
            $scope.searchMap[key]=value;
        }else{
            //如果是規格
            $scope.searchMap.spec[key]=value;
        }
        $scope.search();
    }

    //撤銷
    $scope.removeSearchItem=function (key) {
        //如果点击的是分类或者是品牌
        if (key == 'category' || key == 'brand') {
            $scope.searchMap[key]="";
        }else{
            //如果是規格
            delete $scope.searchMap.spec[key];
        }
        $scope.search();
    }

})