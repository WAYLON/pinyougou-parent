//服务层
app.service('uploadService',function($http){
	    	
	//上传文件
	this.uploadFile=function(){
		var formData = new FormData();
		formData.append('file',file.files[0]);//file 文件上传框name
		return $http({
			method:'POST',
			url:"../upload.do",
			data: formData,
			headers: {'Content-Type':undefined},
			transformRequest: angular.identity
		});

	}

});
