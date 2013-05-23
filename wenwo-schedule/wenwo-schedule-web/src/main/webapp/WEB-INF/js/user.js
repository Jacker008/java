User = {
	init : function(){
		User.binding_events();
		$('#new_user_dialog').dialog({
			autoOpen: false,
			title: '添加用户',
			width: 500,
			resizable: false
		});
		$('#new_user_form').ajaxForm(function(msg){
			if(msg.suc){
				alert(msg.msg);
				$('#new_user_dialog').dialog('close');
				location.reload();
			}
		});
	},
	binding_events : function(){
		$('#new_user').click(function(e){
			e.preventDefault();
			$('#new_user_dialog').dialog('open');
		});
	}
};