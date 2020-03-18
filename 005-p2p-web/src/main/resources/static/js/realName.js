
//同意实名认证协议
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

	//验证手机号
	$("#phone").on("blur", function () {

		var phone = $.trim($("#phone").val());
		if ("" == phone) {
			showError("phone", "请输入手机号");
		} else if (!/^1[1-9]\d{9}$/.test(phone)) {//b) 手机号格式
			//不符合
			showError("phone", "请输入正确的手机号");
		}else {
			showSuccess("phone");
		}
	});

	$("#realName").on("blur",function () {

		var realName=$.trim($("#realName").val());

		if(""==realName){
			showError("realName","请输入真实姓名");
		}else if(!/^[\u4e00-\u9fa5]{0,}$/.test(realName)){
			showError("realName","真实姓名只支持中文");
		}else {
			showSuccess("realName");
		}
	})

	$("#idCard").on("blur",function () {
		var idCard =$.trim($("#idCard").val());
		if(""==idCard){
			showError("idCard","请输入身份证号");
		}else if(!/(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/.test(idCard)){
			showError("idCard","请输入正确的身份证号");
		}else {
			showSuccess("idCard");
		}
	})

	//给获取验证码按钮绑定单击事件
	$("#messageCodeBtn").on("click",function () {
		//获取手机号和密码
		var phone = $.trim($("#phone").val());

		$("#phone").blur();
		$("#realName").blur();
		$("#idCard").blur();

		hideError("messageCode");

		//获取错误提示
		var errorTexts = $("div[id$='Err']").text();

		if ("" == errorTexts) {

			//判断当前是否正在倒计时(通过按钮是否有on这个样式)
			if (!$("#messageCodeBtn").hasClass("on")) {

				//发送ajax请求获取短信是否发送成功的标识
				$.ajax({
					url:basepath+"/loan/messageCode",
					type:"post",
					data:"phone="+phone,
					success:function (data) {
						if (data.code == 1) {
							alert("您的短信验证码是:" + data.data);
							$.leftTime(60,function (d) {
								if (d.status) {
									$("#messageCodeBtn").addClass("on");
									//58秒后获取
									$("#messageCodeBtn").html((d.s == "00"?"60":d.s)+"秒后获取");
								} else {
									$("#messageCodeBtn").removeClass("on");
									$("#messageCodeBtn").html("获取验证码");
								}
							});

						} else {
							showError("messageCode",data.message);
						}
					},
					error:function () {
						showError("messageCode","短信平台异常,请稍后重试");
					}
				});
			}
		}
	});
	$("#messageCode").on("blur",function () {
		var messageCode = $.trim($("#messageCode").val());
		if(""==messageCode){
			showError("messageCode","请输入验证码");
		}else {
			showSuccess("messageCode");
		}
	})

	$("#btnRegist").on("click",function () {

		$("#phone").blur();
		$("#realName").blur();
		$("#idCard").blur();
		$("#messageCode").blur();

		var errorTexts = $("div[id$='Err']").text();

		if ("" == errorTexts) {

			var phone = $.trim($("#phone").val());
			var realName = $.trim($("#realName").val());
			var idCard = $.trim($("#idCard").val());
			var messageCode = $.trim($("#messageCode").val());

			//提交实名认证的请求
			$.ajax({
				url:basepath+"/loan/realName",
				type:"post",
				data:{
					"phone":phone,
					"realName":realName,
					"idCard":idCard,
					"messageCode":messageCode
				},
				success:function (data) {
					if (data.code == 1) {
						window.location.href = basepath+"/index";
					} else {
						$("#messageCode").val("");
						showError("messageCode",data.message);
					}
				},
				error:function () {
					$("#messageCode").val("");
					showError("messageCode","系统繁忙,请稍后重试");
				}
			});
		}

	});
});
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
