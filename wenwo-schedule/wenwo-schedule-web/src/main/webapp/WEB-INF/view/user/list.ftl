[#ftl]
[#import 'common.ftl' as c]
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
		<title>用户列表</title>
		[@c.frame_header /]
		[@c.js file='public/jquery.form.js' /]
		[@c.js file='user.js' /]
		<script type="text/javascript">
			$(function(){
				$('#user_menu').click();
				User.init();
			});
		</script>
	</head>
	<body>
		[@c.frame role=user.role]
			<p><a href="#" id="new_user" style="margin-left:17px">添加用户</a></p>
			<p>
				<table>
					<tr><th>用户名</th><th>角色</th><th>联系邮件</th><th>备注</th><th>创建时间</th></tr>
					[#list users as uItem]
						<tr>
							<td>${uItem.username}</td>
							<td>${uItem.role}</td>
							<td>${uItem.email}</td>
							<td>${uItem.desc}</td>
							<td>${uItem.createTime?string('yyyy-MM-dd HH:mm:ss')}</td>
						</tr>
					[/#list]
				</table>
			</p>
		[/@c.frame]
		<div id="new_user_dialog">
			<form action="/user/create" id="new_user_form" method="post">
				<table border="0">
					<tr><td>用户名: </td><td><input type="text" name="username" class="text_field"/></td></tr>
					<tr><td>密码: </td><td><input type="password" name="password" class="text_field"/></td></tr>
					<tr><td>备注: </td><td><input type="text" name="desc" class="text_field"/></td></tr>
					<tr><td>联系邮件: </td><td><input type="text" name="email" class="text_field"/></td></tr>
					<tr><td colspan="2" align="right"><input type="submit" value="创建用户" /></td></tr>
				</table>
			</form>
		</div>
	</body>
</html>