<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="utf-8"%>
<%@ page language="java" import="com.siesta.model.User"%>

<!-- 一定判断用户是不是第一次登陆 ，是的话，则从cookies中取得记住的用户名密码，不是的话就有服务器转发的。因为服务器转发一般是通过request，session，所以把cookies中的用户名密码放到pageContext中，让el表达式优先取得cookies中的用户名和密码-->
<%
   if(request.getAttribute("user")==null){//用户第一次登陆，不是从后台中回调转发的
	   String userName=null;
	   String password=null;
	   
	   Cookie[] cookies=request.getCookies();
	   for(int i=0;cookies!=null && i<cookies.length;i++){
		   if(cookies[i].getName().equals("user")){
			   userName=cookies[i].getValue().split("-")[0];
			   password=cookies[i].getValue().split("-")[1];
		   }
	   }
	   if(userName==null){
		   userName="";
	   }
	   if(password==null){
		   password="";
	   }
	   //放到pageContext让EL表达式优先获取
	   pageContext.setAttribute("user",new User(userName,password));
   }
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>个人日记本登录</title>
<link href="${pageContext.request.contextPath}/bootstrap/css/bootstrap.css" rel="stylesheet">
<link href="${pageContext.request.contextPath}/bootstrap/css/bootstrap-responsive.css" rel="stylesheet">
<script src="${pageContext.request.contextPath}/bootstrap/js/jQuery.js"></script>
<script src="${pageContext.request.contextPath}/bootstrap/js/bootstrap.js"></script>
<style type="text/css">
      body{
        padding-top:200px;
        padding-bottom: 40px;
        background-color:#fff;
      }
      
      .form-signin-heading{
        text-align:center;
      }
      
      .form-signin{
        max-width:300px;
        padding: 19px 29px 19px;
        margin:0 auto 20px;
        background-color:hsla(1, 45%, 50%, 0.7);
        border:1px solid #e5e5e5;
        -webkit-border-radius:5px;
           -moz-border-radius:5px;
                border-radius:5px;
        -webkit-box-shadow: 0 1px 2px rgba(0,0,0,.05);
           -moz-box-shadow: 0 1px 2px rgba(0,0,0,.05);
                box-shadow: 0 1px 2px rgba(0,0,0,.05);
      }
      
      .form-signin .form-signin-heading,
      .form-signin .checkbox{
        margin-bottom:10px;
      }
      
      .form-signin input[type="text"];
      .form-signin input[type="password"]{
        font-size: 16px;
        height:auto;
        margin-bottom:15px;
        padding:7px 9px;
      }
      
</style>
<script type="text/script">
     function checkForm(){
        var userName=document.getElementById("userName").value();
        var paddword=document.getElementById("password").value();
        if(userName==null || userName==""){
             document.getElementById("error").innerHTML="用户名不能为空";
             return false;
        }
        if(password==null || password==""{   
             document.getElementById("error").innerHTML="密码不能为空";
             return false;
     }
       return true;
}
</script>
</head>
<body>
<div class="container">
      <form name="myForm" class="form-signin" action="login" method="post" onsubmit="return checkForm()">
         <h2 class="form-signin-heading">个人日记本</h2>
         <input id="userName" name="userName" value="${user.userName}" type="text" class="input-block-level" placeholder="登录名称">
         <input id ="password" name="password" value="${user.password}" type="password" calss="input-block-level" placeholder="登录密码">
         <label class="checkbox">
            <input id="remeber" name="remeber" type="checkbox" value="remember-me">记住我&nbsp;&nbsp;&nbsp;&nbsp;<font id="error" color="red">${error}</font>
         </label>
         <button class="btn btn-small btn-primary" type="submin">登录</button>
         <button class="btn btn-small btn-primary" type="reset">重置</button>
     </form>
</body>
</html>