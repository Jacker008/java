ScheduleLib = {
	init : function(){
		ScheduleLib.bindingEvents();
		$('#upload_lib_dialog').dialog({ 
			autoOpen: false,
			title: '上传公共包',
			width: 500,
			resizable: false
		});
	},
	bindingEvents : function(){
		$('#upload_lib_form').ajaxForm(function(msg){
			if(msg.suc){
				alert(msg.msg);
				$('#upload_lib_dialog').dialog('close');
				location.reload();
			}
		});
		$('#new_lib').click(function(e){
			e.preventDefault();
			$('#upload_lib_dialog').dialog('open');
		});
		$('#libs_tb').find('[lib_remove_id]').click(function(e){
			e.preventDefault();
			var libId = $(this).attr('lib_remove_id');
			$.ajax({
				url: '/schedule/remove_lib',
				type: 'get',
				data: {'name': libId},
				success: function(msg){
					if(msg.suc){
						alert(msg.msg);
						location.reload();
					}
				}
			});
		});
	}
};