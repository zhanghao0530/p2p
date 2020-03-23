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


$(function() {

	//验证原密码,失焦判断是否和数据库密码一致
	$("#oldLoginPassword").on("blur", function () {

		var oldLoginPassword = $.trim($("#oldLoginPassword").val());
		if(""==oldLoginPassword){
			showError("oldLoginPassword","请输入原密码");
		}else {
			$.ajax({
				url:basepath+"/loan/checkLoginPassword",
				type:"post",
				data:"oldLoginPassword="+$.md5(oldLoginPassword),
				success:function (data) {
					if(data.code==1){
						showSuccess("oldLoginPassword");
					}else {
						showError("oldLoginPassword",data.message);
					}
				},
				error:function () {
					showError("oldLoginPassword","系统繁忙,请稍候再试")
				}
			})
		}

	})

	//验证新密码
	/*a) 密码不能为空
	b) 密码字符只可使用数字和大小写英文字母
	c) 密码应同时包含英文或数字
	d) 密码应为 6-16 位
	*/
	$("#newLoginPassword").on("blur", function () {
		//获取的密码为明文密码，还需要加密
		var newLoginPassword = $.trim($("#newLoginPassword").val());
		var oldLoginPassword = $.trim($("#oldLoginPassword").val());
		if ("" == newLoginPassword) {
			showError("newLoginPassword", "请输入密码")
		} else if (!/^[0-9a-zA-Z]+$/.test(newLoginPassword)) {
			showError("newLoginPassword", "密码字符只可使用数字和大小写英文字母")
		} else if (!/^(([a-zA-Z]+[0-9]+)|([0-9]+[a-zA-Z]+))[a-zA-Z0-9]*/.test(newLoginPassword)) {
			showError("newLoginPassword", "密码应同时包含英文和数字");
		} else if (newLoginPassword.length < 6 || newLoginPassword.length > 16) {
			showError("newLoginPassword", "密码长度应为6-16位");
		} else if(newLoginPassword == oldLoginPassword){
			showError("newLoginPassword","新密码不能与原密码一致");
		}else{
			showSuccess("newLoginPassword");
		}
	});

	$("#cofirmNewLoginPassword").on("blur",function () {
		var  newLoginPassword = $.trim($("#newLoginPassword").val());
		var cofirmNewLoginPassword=$.trim($("#cofirmNewLoginPassword").val());
		//确认密码不为空
		if(""==cofirmNewLoginPassword){
			showError("cofirmNewLoginPassword","确认密码不能为空");
		} else if(newLoginPassword==cofirmNewLoginPassword){
			showSuccess("cofirmNewLoginPassword");
		}else {
			showError("cofirmNewLoginPassword","两次输入密码不一致");
		}


	})

	$("#phone").on("blur",function () {

		var phone =$.trim($("#phone").val());

		var defaultPhone = $.trim($("#defaultPhone").val());

		if(""==phone){
			showError("phone","请输入手机号")
		}else if (!/^1[1-9]\d{9}$/.test(phone)) {//b) 手机号格式
			//不符合
			showError("phone", "请输入正确的手机号");
		}else {
				showSuccess("phone");



		}
	})

	$("#messageCode").on("blur",function () {
		var phone =$.trim($("#phone").val());
		var defaultPhone = $.trim($("#defaultPhone").val());
		var messageCode = $.trim($("#messageCode").val());
		if(phone==defaultPhone){
			if(""==messageCode){
				showSuccess("messageCode");
			}else {
				$("#messageCode").val("");
				showSuccess("messageCode");
			}
		}else {
			if(""==messageCode){
				showError("messageCode","请输入验证码");
			}else {
				showSuccess("messageCode");
			}
		}

	})


	//点击提交后判断所有验证是否都通过
	$("#btnSubmit").on("click", function () {
		var oldLoginPassword = $.trim($("#oldLoginPassword").val());
		var newLoginPassword = $.trim($("#newLoginPassword").val());
		var cofirmNewLoginPassword=$.trim($("#cofirmNewLoginPassword").val());
		var phone=$.trim($("#phone").val());
		var messageCode=$.trim($("#messageCode").val());

		$("#oldLoginPassword").blur();
		$("#cofirmNewLoginPassword").blur();
		$("#newLoginPassword").blur();
		$("#phone").blur();
		$("#messageCode").blur();




		var errorTexts= $("div[id$='Err']").text();

		if(""==errorTexts){
			$("#oldLoginPassword").val($.md5(oldLoginPassword));
			$("#cofirmNewLoginPassword").val($.md5(cofirmNewLoginPassword));
			$("#newLoginPassword").val($.md5(newLoginPassword));

			$.ajax({
				url:basepath+"/loan/modifyLoginPassword",
				type:"post",
				data:{
					"newLoginPassword":$.md5(newLoginPassword),
					"phone":phone,
					"messageCode":messageCode
				},
				success:function (data) {
					if(data.code==1){
						/*$("#oldLoginPassword").val($.md5(oldLoginPassword));
						$("#cofirmNewLoginPassword").val($.md5(cofirmNewLoginPassword));
						$("#newLoginPassword").val($.md5(newLoginPassword));*/
						window.location.href=basepath+"/loan/page/login";
					}else {
						$("#cofirmNewLoginPassword").val("");
						$("#newLoginPassword").val("");
						showError("messageCode",data.message);
					}
				},
				error:function () {
					showError("messageCode","系统异常,请稍候再试");
				}
			})
		}


	});


	//给获取验证码按钮绑定单击事件
	$("#messageCodeBtn").on("click",function () {
		var phone =$.trim($("#phone").val());
		var loginPassword =$.trim($("#loginPassword").val());
		$("#phone").blur();
		$("#loginPassword").blur();

		hideError("messageCode");
		//判断前项是否验证通过
		var errorText=$("div[id$='Err']").text();
		if(""==errorText){
			if(!$("#messageCodeBtn").hasClass("on")){

				//发起ajax请求
				$.ajax({
					url:basepath+"/loan/messageCode",
					type:"post",
					data:"phone="+phone,
					success:function (data) {
						alert("您的短信验证码是"+data.data)
						if(data.code==1){
							$.leftTime(60,function (d) {
								if(d.status){
									//如果状态为成功,给按钮添加on的样式
									$("#messageCodeBtn").addClass("on");
									$("#messageCodeBtn").html((d.s=="00"?"60":d.s)+"秒后获取")
								}else {
									$("#messageCodeBtn").removeClass("on");
									$("#messageCodeBtn").html("获取验证码");
								}
							})
						}else {
							showError("messageCode",data.message);

						}
					},
					error:function (data) {
						showError("messageCode","短信平台异常，请稍后重试");
					}
				})


			}
		}


	})



})



