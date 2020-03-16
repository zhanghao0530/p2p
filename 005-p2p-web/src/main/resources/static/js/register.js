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
	$("#agree").click(function () {
		var ischeck = document.getElementById("agree").checked;
		if (ischeck) {
			$("#btnRegist").attr("disabled", false);
			$("#btnRegist").removeClass("fail");
		} else {
			$("#btnRegist").attr("disabled", "disabled");
			$("#btnRegist").addClass("fail");
		}
	});
	//验证手机号码
	//a) 手机号不能为空

	$("#phone").on("blur", function () {
		var phone = $.trim($("#phone").val());
		if ("" == phone) {
			showError("phone", "请输入手机号");
		} else if (!/^1[1-9]\d{9}$/.test(phone)) {//b) 手机号格式
			//不符合
			showError("phone", "请输入正确的手机号");
		} else {
			//c) 手机号是否已被注册
			$.ajax({
				url: basepath + "/loan/checkPhone",
				data: "phone=" + phone,
				method: "get",
				success: function (data) {
					if (data.code == 1) {
						showSuccess("phone");
					} else {
						showError("phone", data.message)
					}
				},
				error: function (data) {
					showError("phone", "系统繁忙，请稍候再试")
				}
			});
		}

	})

	//验证密码
	/*a) 密码不能为空
	b) 密码字符只可使用数字和大小写英文字母
	c) 密码应同时包含英文或数字
	d) 密码应为 6-16 位
	*/
	$("#loginPassword").on("blur", function () {
		//获取的密码为明文密码，还需要加密
		var loginPassword = $.trim($("#loginPassword").val());
		if ("" == loginPassword) {
			showError("loginPassword", "请输入密码")
		} else if (!/^[0-9a-zA-Z]+$/.test(loginPassword)) {
			showError("loginPassword", "密码字符只可使用数字和大小写英文字母")
		} else if (!/^(([a-zA-Z]+[0-9]+)|([0-9]+[a-zA-Z]+))[a-zA-Z0-9]*/.test(loginPassword)) {
			showError("loginPassword", "密码应同时包含英文和数字");
		} else if (loginPassword.length < 6 || loginPassword.length > 16) {
			showError("loginPassword", "密码长度应为6-16位");
		} else {
			showSuccess("loginPassword");
		}
	})

	//点击注册后判断所有验证是否都通过
	$("#btnRegist").on("click", function () {
		var phone =$.trim($("#phone").val());
		var loginPassword=$.trim($("#loginPassword").val());
		$("#phone").blur();
		$("#loginPassword").blur();

		/*var flag=true;
		$("div[id$='Err']").each(function () {
			var errorText=$(this).html();
			if(""!=errorText){
				flag=false;
				return false;

			}
			if(flag){
			$.ajax({
				url:basepath+"/loan/user/regesitry",
				type:"get",
				data:{

				}
			})
		}*/
		var errText = $("div[id$='Err']").text();
		if ("" == errText) {
			$("#loginPassword").val($.md5(loginPassword));
			$.ajax({
				url:basepath+"/loan/register",
				type:"post",
				data:{
					"phone":phone,
					"loginPassword":$.md5(loginPassword)
				},
				success:function (data) {
					if(data.code==1){
						//注册成功
						window.location.href=basepath+"/index";
					}else {
						//注册失败
						showError("loginPassword",data.message);
						$("#loginPassword").val("");

					}
				},
				error:function (data) {
					showError("loginPassword","系统繁忙，请稍候再试");
					$("#loginPassword").val("");
				}
			})
		}
	})

		
	})



