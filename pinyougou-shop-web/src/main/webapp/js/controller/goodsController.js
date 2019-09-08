 //控制层 
app.controller('goodsController' ,function($scope,$controller,goodsService,uploadService,itemCatService,typeTemplateService){
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		goodsService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		goodsService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		goodsService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}
	
	//新增
	$scope.add=function(){
		$scope.entity.goodsDesc.introduction=editor.html();
		goodsService.add($scope.entity).success(
			function(response){
				if(response.success){
					alert("新增成功");
					$scope.entity={};
					editor.html("")//清空富文本编辑器
				}else{
					alert(response.message);
				}
			});
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		goodsService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
					$scope.selectIds=[];
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		goodsService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	//上传图片
	$scope.uploadFile=function(){
		uploadService.uploadFile().success(function(response) {
			if(response.success){//如果上传成功，取出url
				$scope.image_entity.url=response.message;//设置文件地址
			}else{
				alert(response.message);
			}
		}).error(function() {
			alert("上传发生错误");
		});
	};

	$scope.entity={goods:{},goodsDesc:{itemImages:[]}};//定义页面实体结构
	//添加图片列表
	$scope.add_image_entity=function(){
		$scope.entity.goodsDesc.itemImages.push($scope.image_entity);
	}
	//列表中移除图片
	$scope.remove_image_entity=function(index){
		$scope.entity.goodsDesc.itemImages.splice(index,1);
	}
	//查询一级商品分类列表
	$scope.selectItemCat1List=function () {
		itemCatService.findByParentId(0).success(
			function (response) {
			$scope.itemCat1List=response;
		});
	}
	//查询二级商品分类列表
	$scope.$watch('entity.goods.category1Id',function (newValue,oldValue) {
		itemCatService.findByParentId(newValue).success(
			function (response) {
				$scope.itemCat2List=response;
			});
	});
	//查询三级商品分类列表
	$scope.$watch('entity.goods.category2Id',function (newValue,oldValue) {
		itemCatService.findByParentId(newValue).success(
			function (response) {
				$scope.itemCat3List=response;
			});
	});
	//读取模板id
	$scope.$watch('entity.goods.category3Id',function (newValue,oldValue) {
		itemCatService.findOne(newValue).success(
			function (response) {
				$scope.entity.goods.typeTemplateId=response.typeId;
			});
	});
	//读取模板id后，读取品牌列表
	$scope.$watch('entity.goods.typeTemplateId',function (newValue,oldValue) {
		typeTemplateService.findOne(newValue).success(
			function (response) {
				$scope.typeTemplate=response;//模板对象
				//品牌列表类型转换
				$scope.typeTemplate.brandIds=JSON.parse($scope.typeTemplate.brandIds);
				//扩展属性
				$scope.entity.goodsDesc.customAttributeItems = JSON.parse($scope.typeTemplate.customAttributeItems);
			});
	});

});	
