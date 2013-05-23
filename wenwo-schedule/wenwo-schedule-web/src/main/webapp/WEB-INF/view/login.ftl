[#ftl]
[#import 'common.ftl' as c ]
<html lang="cn">
    <head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
        <title>Wenwo schedule system login</title>
        [@c.css file='style.css' /]
    </head>
    <body>
		<form class="form-5 clearfix" action="login" method="post" >
		    <p>
		        <input type="text" id="login" name="username" placeholder="帐号">
		        <input type="password" name="password" id="password" placeholder="密码"> 
		    </p>
		    <button type="submit" name="submit">
		    	<i class="icon-arrow-right"></i>
		    	<span>登录</span>
		    </button>
		</form>​​​​
    </body>
</html>