//错误提示
function showError(id,msg) {
	$("#"+id+"Ok").hide();
	$("#"+id+"Err").html("<i></i><p>"+msg+"</p>");
	$("#"+id+"Err").show();
	$("#"+id).addClass("input-red");
}
//错误隐藏
function hideError(id) {
	$("#"+id+"Err").hide();
	$("#"+id+"Err").html("");
	$("#"+id).removeClass("input-red");
}
//显示成功
function showSuccess(id) {
	$("#"+id+"Err").hide();
	$("#"+id+"Err").html("");
	$("#"+id+"Ok").show();
	$("#"+id).removeClass("input-red");
}


//打开注册协议弹层
function alertBox(maskid,bosid){
	$("#"+maskid).show();
	$("#"+bosid).show();
}
//关闭注册协议弹层
function closeBox(maskid,bosid){
	$("#"+maskid).hide();
	$("#"+bosid).hide();
}

//注册协议确认
$(function() {
	$("#agree").click(function(){
		var ischeck = document.getElementById("agree").checked;
		if (ischeck) {
			$("#btnRegist").attr("disabled", false);
			$("#btnRegist").removeClass("fail");
		} else {
			$("#btnRegist").attr("disabled","disabled");
			$("#btnRegist").addClass("fail");
		}
	});
	//验证手机号码
	//a) 手机号不能为空

	$("#phone").on("blur",function () {
		var phone =$.trim($("#phone").val());
		if(""==phone){
			showError("phone","请输入手机号");
		}else if(!/^1[1-9]\d{9}$/.test(phone)){//b) 手机号格式
				//不符合
				showError("phone","请输入正确的手机号");
		}else {
			//c) 手机号是否已被注册
			$.ajax({
				url:"/loan/checkPhone",
				data:{"phone":phone},
				method:"get",
				success:function (data) {
					if(data.code==1){
						showSuccess("phone");
					}else {
						showError("phone",data.message)
					}
				},
				error:function (data) {
					showError("phone","系统繁忙，请稍候再试")
				}
			});
		}

	})




});
