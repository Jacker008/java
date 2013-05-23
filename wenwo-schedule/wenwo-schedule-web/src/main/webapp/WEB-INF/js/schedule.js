/** 调度js脚本 **/
Schedule = {
	activePanel : null,
	init : function() {
		Schedule.binding_events();
		var tabs = $('#tabs');
		tabs.tabs({
			activate: function(event, ui){
				Schedule.activePanel = ui.newPanel;
				Schedule.reloadCurrPanel();
			}
		});
		Schedule.activePanel = tabs.children('div')[0];
		Schedule.reloadCurrPanel();
	},
	binding_events : function() {
		var newJobDialog = $('#new_job_dialog');
		var jobDetailDialog = $('#job_detail_dialog');
		var jobUpdateDialog = $('#job_update_dialog');
		
		newJobDialog.dialog({ 
			autoOpen: false,
			title: '添加新任务',
			width: 500,
			resizable: false
		});
		jobDetailDialog.dialog({ 
			autoOpen: false,
			title: '任务信息',
			width: 500,
			resizable: false
		});
		jobUpdateDialog.dialog({ 
			autoOpen: false,
			title: '修改任务信息',
			width: 500,
			resizable: false
		});
		$('#new_job_cron').tabs({
			heightStyle: "fill"
		});
		$('#new_job').click(function(e){
			e.preventDefault();
			newJobDialog.dialog('open');
		});
		$('#new_job_form').ajaxForm(function(resp){
			if(resp.suc){
				alert(resp.msg);
				newJobDialog.dialog('close');
				Schedule.reloadCurrPanel();
			}
		});
		Schedule.bindingJobsEvents();
	},
	bindingJobsEvents: function(){
		var jobDetailDialog = $('#job_detail_dialog');
		var jobUpdateDialog = $('#job_update_dialog');
		
		var jobsTable = $('[name=jobs_tb]');
		jobsTable.find('a').unbind();
		// 查看任务信息
		jobsTable.find('a[view_id]').click(function(e){
			e.preventDefault();
			var jobId = $(this).attr('view_id');
			$.ajax({
				url: '/schedule/detail',
				type: 'get',
				data: {'jobId': jobId},
				success: function(msg){
					jobDetailDialog.html(msg);
					jobDetailDialog.dialog('open');
				}
			});
		});
		// 修改任务信息
		jobsTable.find('a[update_id]').click(function(e){
			e.preventDefault();
			var jobId = $(this).attr('update_id');
			$.ajax({
				url: '/schedule/before_update',
				type: 'get',
				data: {'jobId': jobId},
				success: function(msg){
					jobUpdateDialog.html(msg);
					$('#update_job_cron').tabs({
						heightStyle: "fill"
					});
					CronPicker.init('update_job_cron');
					jobUpdateDialog.dialog('open');
					$('#update_job_form').ajaxForm(function(resp){
						if(resp.suc){
							alert(resp.msg);
							jobUpdateDialog.dialog('close');
							Schedule.reloadCurrPanel();
						}
					});
				}
			});
		});
		// 删除任务
		jobsTable.find('a[remove_id]').click(function(e){
			e.preventDefault();
			var jobId = $(this).attr('remove_id');
			var jobDesc = $(this).attr('job_desc');
			if(window.confirm("确认删除任务 ["+jobDesc+"] 吗？")){
				$.ajax({
					url: '/schedule/remove',
					type: 'get',
					data: {'jobId': jobId},
					success: function(msg){
						alert(msg.msg);
						Schedule.reloadCurrPanel();
					}
				});
			}
		});
		// 暂停任务
		jobsTable.find('a[pause_id]').click(function(e){
			e.preventDefault();
			var jobId = $(this).attr('pause_id');
			$.ajax({
				url: '/schedule/pause',
				type: 'get',
				data: {'jobId': jobId},
				success: function(msg){
					alert(msg.msg);
					Schedule.reloadCurrPanel();
				}
			});
		});
		// 启动任务
		jobsTable.find('a[resume_id]').click(function(e){
			e.preventDefault();
			var jobId = $(this).attr('resume_id');
			$.ajax({
				url: '/schedule/resume',
				type: 'get',
				data: {'jobId': jobId},
				success: function(msg){
					alert(msg.msg);
					Schedule.reloadCurrPanel();
				}
			});
		});
		// 手动任务
		jobsTable.find('a[trigger_id]').click(function(e){
			e.preventDefault();
			var jobId = $(this).attr('trigger_id');
			if(window.confirm("确认手动执行任务"+jobId+"吗？")){
				$.ajax({
					url: '/schedule/trigger',
					type: 'get',
					data: {'jobId': jobId},
					success: function(msg){
						alert(msg.msg);
						Schedule.reloadCurrPanel();
					}
				});
			}
		});
	},
	reloadCurrPanel: function(){
		var currPanel = Schedule.activePanel;
		var owner = $(currPanel).attr('id');
		$.ajax({
			url: '/schedule/refresh_list',
			type: 'get',
			data: {'owner': owner},
			success: function(msg){
				$(currPanel).html(msg);
				Schedule.bindingJobsEvents();
			}
		});
	}
};