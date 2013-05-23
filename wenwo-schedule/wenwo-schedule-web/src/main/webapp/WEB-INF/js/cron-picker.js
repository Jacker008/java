CronPicker = {
	init : function(tabsId){
		CronPicker.bindingEvents(tabsId);
	},
	bindingEvents : function(tabsId){
		var tabs = $('#' + tabsId);
		tabs.unbind();
		CronPicker.manualActiver(tabs, 'week', 'weeks', 5);
		CronPicker.manualActiver(tabs, 'month', 'months', 4);
		CronPicker.manualActiver(tabs, 'day', 'days', 3);
		CronPicker.manualActiver(tabs, 'hour', 'hours', 2);
		CronPicker.detailActiver(tabs, 'minute', 1);
		CronPicker.detailActiver(tabs, 'second', 0);
	},
	manualActiver : function(parentPanel, controller, actions, cronIndex){
		var actionEles = parentPanel.find('input[name='+actions+']');
		actionEles.attr('disabled', 'disabled');
		parentPanel.find('input[name='+controller+']').change(function(e){
			e.preventDefault();
			actionEles.unbind();
			if(parentPanel.find('input[name='+controller+']:checked').val() == 'manual'){
				actionEles.removeAttr('disabled');
				CronPicker.updateCronWithCheckboxVals(parentPanel, actionEles, cronIndex);
				actionEles.change(function(e){
					CronPicker.updateCronWithCheckboxVals(parentPanel, actionEles, cronIndex);
				});
			}else{
				actionEles.attr('disabled', 'disabled');
				CronPicker.updateCronExp(parentPanel, '*', cronIndex);
			}
		});
	},
	detailActiver : function(parentPanel, controller, cronIndex){
		parentPanel.find('input[name='+controller+']').change(function(e){
			var checkedSecond = parentPanel.find('input[name='+controller+']:checked');
			var checkedParent = checkedSecond.parent().parent();
			var timeStart = checkedParent.find('[name=time_start]');
			var timeEvery = checkedParent.find('[name=time_every]');
			timeStart.unbind();
			timeEvery.unbind();
			if(checkedSecond.val() == 'every'){
				timeStart.attr('disabled', 'disabled');
				timeEvery.attr('disabled', 'disabled');
				CronPicker.updateCronExp(parentPanel, '*', cronIndex);
			}else{
				timeStart.removeAttr('disabled');
				timeEvery.removeAttr('disabled');
				timeStart.keyup(function(e){
					CronPicker.updateCronWithDoubleText(parentPanel, timeStart, timeEvery, cronIndex);
				});
				timeEvery.keyup(function(e){
					CronPicker.updateCronWithDoubleText(parentPanel, timeStart, timeEvery, cronIndex);
				});
				CronPicker.updateCronWithDoubleText(parentPanel, timeStart, timeEvery, cronIndex);
			}
		});
	},
	updateCronExp : function(rootPanel, subCron, inx){
		var cronInput = rootPanel.find('[name=cron]');
		var cronParts = cronInput.val().split(' ');
		cronParts[inx] = subCron;
		cronInput.val(cronParts.join(' '));
	},
	checkboxGroupVal : function(cbGroup){
		var values = new Array();
		$.each(cbGroup, function(index){
			if($(this).attr('checked') != undefined){
				values.push($(this).val());
			}
		});
		return values;
	},
	updateCronWithCheckboxVals : function(parentPanel, actionEles, cronIndex) {
		var chosenVals = CronPicker.checkboxGroupVal(actionEles);
		var valCount = chosenVals.length;
		if(valCount == 0){
			CronPicker.updateCronExp(parentPanel, '*', cronIndex);
		}else{
			var cron = '';
			var lastVal;
			var lasting = false;
			$.each(chosenVals, function(index){
				var currVal = parseInt($(this)[0]);
				if(index == 0){
					cron += currVal;
				}else{
					if(currVal-lastVal>1){
						if(lasting){
							cron += ('-'+lastVal);
						}
						cron += (','+currVal);
						lasting = false;
					}else{
						if(index == valCount-1){
							cron += ('-'+currVal);
						}
						lasting = true;
					}
				}
				lastVal = currVal;
			});
			CronPicker.updateCronExp(parentPanel, cron, cronIndex);
		}
	},
	updateCronWithDoubleText : function(parentPanel, timeStart, timeEvery, cronIndex){
		if(parseInt(timeEvery.val()) > 0){
			CronPicker.updateCronExp(parentPanel, timeStart.val() + '/' + timeEvery.val(), cronIndex);
		}else{
			CronPicker.updateCronExp(parentPanel, timeStart.val(), cronIndex);
		}
	}
};