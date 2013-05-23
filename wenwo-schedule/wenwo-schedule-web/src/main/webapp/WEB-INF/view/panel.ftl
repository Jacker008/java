[#ftl]
[#import 'common.ftl' as c]
<html>
	<head>
		<title>管理面板</title>
		[@c.frame_header /]
	</head>
	<body>
		[@c.frame role=user.role]
			Hello ${user.username}!
		[/@c.frame]
	</body>
</html>