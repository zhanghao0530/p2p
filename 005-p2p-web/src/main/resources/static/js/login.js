var referrer = "";//登录后返回页面
referrer = document.referrer;
if (!referrer) {
	try {
		if (window.opener) {                
			// IE下如果跨域则抛出权限异常，Safari和Chrome下window.opener.location没有任何属性              
			referrer = window.opener.location.href;
		}  
	} catch (e) {
	}
}

//按键盘Enter键即可登录
$(document).keyup(function(event){
	if(event.keyCode == 13){
		login();
	}
});

$(function () {


	$("#messageCodeBtn").on("click", function () {

		//判断手机号
		var phone = $.trim($("#phone").val());
		var loginPassword = $.trim($("#loginPassword").val());
		if ("" == phone) {
			$("#showId").html("请输入手机号");
		} else if (!/^1[1-9]\d{9}$/.test(phone)) {
			$("#showId").html("手机号格式不正确，请重新输入");
		} else if ("" == loginPassword) {
			$("#showId").html("请输入密码");
		} else {
			$("#showId").html("");

			//判断当前是否正在倒计时（通过按钮是否有这个样式）
			if (!$("#messageCodeBtn").hasClass("on")) {
				$.ajax({
					url: basepath + "/loan/messageCode",
					type: "post",
					data: "phone=" + phone,
					success: function (data) {
						if (data.code == 1) {
							alert("你的验证码是：" + data.data);
							$.leftTime(60, function (d) {
								if (d.status) {
									$("#messageCodeBtn").addClass("on");

									$("#messageCodeBtn").html((d.s == "00" ? "60" : d.s) + "秒后获取");

								} else {
									$("#messageCodeBtn").removeClass("on");
									$("#messageCodeBtn").html("获取验证码");
								}
							});
						} else {
							$("#showId").html(data.message);
						}
					},
					error: function () {
						$("#showId").html("系统繁忙，请稍候再试")
					}
				})
			}
		}


	})

	$("#loginBtn").on("click",function () {
		var phone =$.trim($("#phone").val());
		var loginPassword =$.trim($("#loginPassword").val());
		var messageCode = $.trim($("#messageCode").val());
		if(""==phone){
			$("#showId").html("请输入手机号");
		}else if(!/^1[1-9]\d{9}$/.test(phone)){
			$("#showId").html("手机号格式不正确，请重新输入");
		}else if(""==loginPassword) {
			$("#showId").html("请输入密码");
		}else if(""==messageCode){
			$("#showId").html("请输入短信验证码");
		}else {
			$("#showId").html("");
			$("#loginPassword").val($.md5(loginPassword));
			$.ajax({
				url:basepath+"/loan/login",
				type:"post",
				data:{
					phone:phone,
					loginPassword:$.md5(loginPassword),
					messageCode:messageCode
				},
				success:function (data) {
					if(data.code==1){
						var redirectUrl=$("#redirectUrl").val();
						window.location.href=redirectUrl;
					}else {
						$("#loginPassword").val("");
						$("#messageCode").val("");
						$("#showId").html(data.message);
					}
				},
				error:function () {
					$("#loginPassword").val("");
					$("#messageCode").val("");
					$("#showId").html("用户名或密码有误");
				}
			})
		}
	})

})