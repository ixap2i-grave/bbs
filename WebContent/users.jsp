<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page isELIgnored = "false"%>
<%@taglib prefix ="c" uri = "http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix ="fmt" uri = "http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>ユーザー管理画面</title>
	<link href="bbs.css" rel="stylesheet" type="text/css">
</head>
<body>
<div class = "main-contents">
	<a href="./">戻る</a>
	<a href="signup">新規登録</a>
<br />
<br />
<div class="title">
	<div class="title"><h2><c:out value="ユーザー管理画面" /></h2></div>
</div>
<br />
<br />
<div class="users">
<%--userをfor文で回してIDと名前を一覧表示したい --%>
<center>
	<TABLE cellpadding="15">
<tr>
<th>名前</th>
<th>ユーザー編集</th>
<th>ログインID</th>
<th>アカウント</th>

	<c:forEach items="${users}" var="user">
		<TR><TD>
			<div class="message">
			<div class="account-name">
				<a href="settings?id=${user.id}" >
				<span class="name"><c:out value="${user.name}" /></span></a></div>
			</TD>
			<TD>
				<a href="settings?id=${user.id}" >
				<button name="stop" type="submit" style="margin-left:25px;">
				編集</button></a>
			</TD>
			<TD>
				<c:out value="${user.loginId}" />
			</TD>
			<TD>
			<c:if test="${ user.id != loginUser.id }">
				<form action="stopuser" method="post">
				<input type="hidden" name="userid" value="${user.id}">
				<c:if test="${ user.stop  == true }">
					<button name="stop" type="submit" value="false" onclick="alert('アカウントを停止しますか？')"  style="margin-left:25px;">停止</button>
					<input type ="hidden" name="id" value="${user.id}">
					<input type ="hidden" name="stop" value="${user.stop}">
				</c:if>
				<c:if test="${ user.stop  == false }">
					<button name="stop" type="submit"  value="true" onclick="alert('アカウントを復活させますか？')" style="margin-left:25px;">復活</button>
					<input type ="hidden" name="id" value="${user.id}">
					<input type ="hidden" name="stop" value="${user.stop}">
				</c:if>
				</form>
			</c:if>
			</TD>
			</TR>
			</c:forEach>
	</TABLE>
	<br />
</center>
</div>
<br />
<br />
<div class = "copyright">Copyright(c)Akane Yamashita</div>
</div>
</body>
</html>