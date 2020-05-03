var tablename;
var elemId;
var totalNum;
var currentPage=1;

$('body').on('focus',".datepicker_recurring_start", function(){
    $(this).datetimepicker({    defaultDate: new Date(),
    format: 'YYYY-MM-DD HH:mm:ss', sideBySide: true});
});

function getData (data, l1, l2) {
	tablename = data;
	setActiveButton(data);
	$.getJSON("http://soc-web-liv-82.napier.ac.uk/api/" + data, function(result){
    	totalNum = result.length;
    	$.getJSON("http://soc-web-liv-82.napier.ac.uk/api/" + data + "?limit1="+l1+"&limit2="+l2, function(result){
    		makeTable(result, l1, l2);
    	});
    });
}

function getHeaders(result){
	var headers = [];
    	$.each(result, function(key, value){
    		headers.push(key);
    		//console.log(key);
    	});
    return headers;
}

function makeTable(result, l1, l2){
	var modalIndex = 0;
	$('#pageHeader').html(tablename.charAt(0).toUpperCase()+tablename.slice(1));
	$('#pageContent').css("margin-right","15px");
   
    table = '<div style="margin-left:5px;" class="row"><div class="col-xl-3 col-md-6 mb-4"><div class="card card-hover border-left-primary shadow h-10 py-2" data-toggle="modal" data-target="#addModal" style="cursor: pointer;"><div class="card-body"><div class="row no-gutters align-items-center"><div class="col mr-2"><div class="text-xs font-weight-bold text-primary text-uppercase mb-1">Add</div><div class="h5 mb-0 font-weight-bold text-gray-800">Create Record</div></div><div class="col-auto"><i class="fas fa-plus fa-2x text-gray-300"></i></div></div></div></div></div>';

    table += '<div class="col-xl-3 col-md-6 mb-4"><div class="card border-left-success shadow h-10 py-2"><div class="card-body"><div class="row no-gutters align-items-center"><div class="col mr-2"><div class="text-xs font-weight-bold text-success text-uppercase mb-1">Number of '+tablename+'</div><div class="h5 mb-0 font-weight-bold text-gray-800">'+totalNum+'</div></div><div class="col-auto"><i class="fas fa-warehouse fa-2x text-gray-300"></i></div></div></div></div></div></div></div>';
     
    table += '<p style="margin-left:18px;">The table does not show all the values in a record. Click on a record to see them all and perform updates.</p>';
	//table += '<div class="row"><div class="col-sm"><button style="display:inline-block;width:250px;margin-top:10px;margin-left:10px;" class="btn btn-dark" data-toggle="modal" data-target="#addModal">New</button></div><div class="col-sm"><h3 style="display:inline-block;margin-top:10px;margin-left:10px;">'+tablename.charAt(0).toUpperCase()+tablename.slice(1)+'</h3></div><div class="col-sm"><h4 style="margin-top:10px;margin-left:10px;">Total: '+result.length+'</h4></div></div><br>';

	table += '<div style="width:auto;margin:18px;" class="card border-left-warning shadow"><table class="table table-hover"><thead><tr>';
	var headers = getHeaders(result[0]);
	var modals = makeModals(result);
	var picModals = '';

    var i=0;
	for (var header in headers) {
		if (i<8){
			table += '<th scope="col">' + headers[header] + '</th>';
			i++;
		}
	}
	table += '</tr></thead><tbody>';

    	$.each(result, function (key, value) {
    		table += '<tr>';
    		i=0;
    		for (var index in value) {
        		//console.log(value[index]);
        		if (i<8){
        			var t = '' + value[index];
        			var picId = 1;
        			if (t.toLowerCase().includes('.jpg') || t.toLowerCase().includes('.png'))
        			{
        				table += '<td><button style="margin-left:5px;" data-toggle="modal" data-target="#modalPic'+picId+'" class="btn btn-primary"><i class="fas fa-camera"></i> View picture</button></td>';

        				picModals += '<div class="modal fade" id="modalPic'+picId+'" tabindex="-1" role="dialog" aria-labelledby="myModalLabel"><div class="modal-dialog" role="document"><div class="modal-content"><div class="modal-header"><h4 class="modal-title" id="myModalLabel">Receipt Picture</h4><button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button></div><div class="modal-body"><img style="display:block;width:80%;margin:auto;" src="'+value[index]+'" /></div><div class="modal-footer"><button type="button" class="btn btn-dark" data-dismiss="modal">Close</button></div></div></div></div>';
        				picId++;
        			}
        			else
        			{
        				table += '<td data-toggle="modal" data-target="#modal'+modalIndex+'" style="cursor: pointer;">' + value[index] + '</td>';
        			}
        			
        			i++;
        	 	}
    		}
    		table += '</tr>';
			modalIndex++;
    	});

    table += '</tbody></table></div>';
	table = table.replace(/null/g,'');
	currentPage=1;
	var totalPages = Math.ceil(totalNum/10);
	if (l2==0)
	{
		if (result.length<10)
		{
			table += '<footer><div><div><h3 style="cursor:default;" class="d-inline"><span class="badge badge-secondary"><i class="fas fa-angle-left"></i></span></h3>';
			table += '&nbsp;<h3 style="cursor:default;" class="d-inline"><span class="badge badge-secondary"><i class="fas fa-angle-right"></i></span></h3><div style="width:150px;">Page '+currentPage+' of '+totalPages+'</div></div></div></footer>';
		}
		else
		{
			table += '<footer><div><div><h3 style="cursor:default;" class="d-inline"><span class="badge badge-secondary"><i class="fas fa-angle-left"></i></span></h3>';
			l2+=10;
			table += '&nbsp;<h3 class="d-inline" onclick="getData(\''+tablename+'\','+l1+','+l2+');currentPage++;"><span class="badge badge-primary"><i class="fas fa-angle-right"></i></span></h3><div style="width:150px;">Page '+currentPage+' of '+totalPages+'</div></div></div></footer>';
		}
	}
	else
	{
		if (result.length<10)
		{
			l2-=10;
			table += '<footer><div><div><h3 class="d-inline" onclick="getData(\''+tablename+'\','+l1+','+l2+');"><span class="badge badge-primary"><i class="fas fa-angle-left"></i></span></h3>';
			table += '&nbsp;<h3 style="cursor:default;" class="d-inline"><span class="badge badge-secondary"><i class="fas fa-angle-right"></i></span></h3><div style="width:150px;">Page '+currentPage+' of '+totalPages+'</div></div></div></footer>';
		}
		else
		{
			l2-=10;
			table += '<footer><div><div><h3 class="d-inline" onclick="getData(\''+tablename+'\','+l1+','+l2+');"><span class="badge badge-primary"><i class="fas fa-angle-left"></i></span></h3>';
			l2+=20;
			table += '&nbsp;<h3 class="d-inline" onclick="getData(\''+tablename+'\','+l1+','+l2+');"><span class="badge badge-primary"><i class="fas fa-angle-right"></i></span></h3><div style="width:150px;">Page '+currentPage+' of '+totalPages+'</div></div></div></footer>';
		}
	}

    $("#pageContent").html(table);
	$("#modals").html(modals + picModals);
}

function makeModals(result){
	var modal = '';
	var modalIndex = 0;
	var elemId;
	
	$.each(result, function (key, value) {
    	modal += '<div class="modal fade" id="modal' + modalIndex + '" tabindex="-1" role="dialog" aria-hidden="true">';
		modal += '<div class="modal-dialog modal-xl" role="document">';
		modal += '<div class="modal-content">';
		modal += '<div class="modal-header">';
		modal += '<h5 class="modal-title">Update Record</h5>';
		modal += '<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>';
		modal += '</div>';
		modal += '<div class="modal-body"><form name="form'+modalIndex+'">';
		
		var oddCheck = 0;
		first_iteration = true;
    	for (var index in value) {
			if (first_iteration){elemId=value[index];first_iteration=false;modal+='<input type="hidden" id="elemId'+modalIndex+'" value="'+elemId+'">'}
			else
			{
				console.log(index + " - oddCheck: " + oddCheck);
				var disabled = '';
				if (index.toLowerCase().includes('datecreated') || index.toLowerCase().includes('lastconnected'))
				{
						disabled = 'disabled="disabled"';
				}

				if (oddCheck%2==1)
				{
					if (index.toLowerCase().includes('date') || index.toLowerCase().includes('lastconnected'))
					{
						modal +=  '<div class="row"><div class="col-sm"><label>' + index + '</label></div><div class="col-sm"><div id="date" class="input-group date"><input type="text" '+disabled+' class="form-control datepicker_recurring_start" name="' +  index + '" value="' + value[index]+' "/><div class="input-group-append"><span class="input-group-text" id="basic-addon2"><i class="fas fa-calendar-alt"></i></span></div></div></div>';
					}
					else
					{
						modal +=  '<div class="row"><div class="col-sm"><label>' + index + '</label></div><div class="col-sm"><input class="form-control" type="text" name="' +  index + '" value="' + value[index] + '" /></div>';
					}
				}
				else
				{
					if (index.toLowerCase().includes('date') || index.toLowerCase().includes('lastconnected'))
					{
						modal +=  '<div class="col-sm"><label>' + index + '</label></div><div class="col-sm"><div id="date" class="input-group"><input type="text" '+disabled+' class="form-control datepicker_recurring_start" name="' +  index + '" value="' + value[index]+' "/><div class="input-group-append"><span class="input-group-text" id="basic-addon2"><i class="fas fa-calendar-alt"></i></span></div></div></div></div><br>';
					}
					else
					{
        				modal +=  '<div class="col-sm"><label>' + index + '</label></div><div class="col-sm"><input class="form-control" type="text" name="' +  index + '" value="' + value[index] + '" /></div></div><br>';
					}
				}
			}
			oddCheck++;
    	}
    	oddCheck--;
    	if(oddCheck%2!=0)
    	{
    		modal+= '<div class="col-sm"></div><div class="col-sm"></div></div>';
    	}
    	
		modal += '</form></div><div class="modal-footer"><button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button><button type="button" class="btn btn-danger" data-toggle="modal" onclick="getRowId(\'elemId'+modalIndex+'\');" data-target="#deleteModal" data-dismiss="modal">Delete</button><button type="button" class="btn btn-primary" data-dismiss="modal" onclick="updateRecord('+modalIndex+','+elemId+',\''+tablename+'\');">Save changes</button></div></div></div></div>';
		modalIndex++;
    });
	
	var headers = getHeaders(result[0]);
	
	modal += '<div class="modal fade" id="addModal" tabindex="-1" role="dialog" aria-hidden="true"><div class="modal-dialog modal-xl" role="document"><div class="modal-content"><div class="modal-header"><h5 class="modal-title">Create Record</h5><button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button></div><div class="modal-body"><form name="createForm" id="createForm">';

	var first_iteration = true;
	oddCheck = 0;
	for (var header in headers) {
		if (first_iteration){first_iteration=false;}
		else
		{
		//modal += '<div class="row"><div class="col-sm"><label>' + headers[header] + '</label></div><div class="col-sm"><input type="text" name="' +  headers[header] + '" /></div></div><br>';

				disabled = '';
				if (headers[header].toLowerCase().includes('datecreated') || headers[header].toLowerCase().includes('lastconnected'))
				{
						disabled = 'disabled="disabled"';
				}

				if (oddCheck%2==1)
				{
					if (headers[header].toLowerCase().includes('date') || headers[header].toLowerCase().includes('lastconnected'))
					{
						modal +=  '<div class="row"><div class="col-sm"><label>' + headers[header] + '</label></div><div class="col-sm"><div id="date" class="input-group"><input type="text" '+disabled+' class="form-control datepicker_recurring_start" name="' +  headers[header] + '" /><div class="input-group-append"><span class="input-group-text" id="basic-addon2"><i class="fas fa-calendar-alt"></i></span></div></div></div>';
					}
					else
					{
						modal +=  '<div class="row"><div class="col-sm"><label>' + headers[header] + '</label></div><div class="col-sm"><input class="form-control" type="text" name="' +  headers[header] + '" /></div>';
					}
				}
				else
				{
					if (headers[header].toLowerCase().includes('date') || headers[header].toLowerCase().includes('lastconnected'))
					{
						modal +=  '<div class="col-sm"><label>' + headers[header] + '</label></div><div class="col-sm"><div id="date" class="input-group"><input type="text" '+disabled+' class="form-control datepicker_recurring_start" name="' +  headers[header] + '" /><div class="input-group-append"><span class="input-group-text" id="basic-addon2"><i class="fas fa-calendar-alt"></i></span></div></div></div></div><br>';
					}
					else
					{
        				modal +=  '<div class="col-sm"><label>' + headers[header] + '</label></div><div class="col-sm"><input class="form-control" type="text" name="' +  headers[header] + '" /></div></div><br>';
					}
				}
    	}
    	oddCheck++;
	}
		oddCheck--;
	    if(oddCheck%2!=0)
    	{
    		modal+= '<div class="col-sm"></div><div class="col-sm"></div></div>';
    	}
	
	modal += '</form></div><div class="modal-footer"><button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button><button type="button" class="btn btn-primary" data-dismiss="modal" onclick="createRecord(\''+tablename+'\');">Create Record</button></div></div></div></div>';
	
	modal += '<div class="modal fade" id="deleteModal" tabindex="-1" role="dialog" aria-hidden="true"><div class="modal-dialog" role="document"><div class="modal-content"><div class="modal-header"><h5 class="modal-title">Delete Record</h5><button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button></div><div class="modal-body"><p>Are you sure you want to delete this record?<br>This cannot be undone.</p></div><div class="modal-footer"><button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button><button type="button" class="btn btn-danger" data-dismiss="modal" onclick="deleteRecord(\''+tablename+'\');">Delete</button></div></div></div></div>';
	
	modal = modal.replace(/null/g,'');
	
	return modal;
}


function updateRecord(formId, elemId, table){
	$('form:eq('+formId+') *').filter(':input').each(function () {
		if (this.value == ''){this.value='None';}
		if (this.value == ' '){this.value='None';}
	});
	var str = $('form:eq('+formId+')').serialize();
	str = str.replace(/null/g,'None');
	str = str.replace(/\s/g, "None");
	console.log(str);
	$.ajax({
	  method: "PUT",
	  url: "http://soc-web-liv-82.napier.ac.uk/api/" + table + "/" + elemId + "?" + str,
	  success: function(dat) {
	  		getData(table,10,0);
		}
	});
}

function deleteRecord(table){
	$.ajax({
	  method: "DELETE",
	  url: "http://soc-web-liv-82.napier.ac.uk/api/" + table + "/" + elemId,
	  success: function(dat) {
	  		 getData(table,10,0);
		}
	});
}

function createRecord(table){
	$('form[name="createForm"] *').filter(':input').each(function () {
		if (this.value == ''){this.value='None';}
	});
	var str = $('form[name="createForm"]').serialize();
	str = str.replace(/null/g,'None');
	str = str.replace(/\s/g, 'None');
	$.ajax({
	  method: "POST",
	  url: "http://soc-web-liv-82.napier.ac.uk/api/" + table + "?" + str,
	  success: function(dat) {
	  		getData(table,10,0);
		}
	});	
}

function getRowId(elemModal)
{
	elemId = $('#'+elemModal).val();
}

function setActiveButton(id)
{
	$(".nav-item").removeClass("active");
	$("#nav-"+id).addClass("active");
}

function changePassword()
{
	var oldpas = $('#oldPassword').val();
	var newpas1 = $('#newPassword1').val();
	var newpas2 = $('#newPassword2').val();
	if (newpas1==newpas2)
	{
		$.ajax({
		  method: "PUT",
		  url: "http://soc-web-liv-82.napier.ac.uk/admin/changepassword?oldPassowrd="+oldpas+"&newPassword="+newpas1,
		  success: function(dat) {
				if(dat.Status=='Error')
				{
					$('#changePasswordMessage').text(dat.Message);
					$('#oldPassword').val('');
					$('#newPassword1').val('');
					$('#newPassword2').val('');
				}
				else
				{
					$('#changePasswordModal').modal('toggle');
					alert(dat.Message);
					$('#oldPassword').val('');
					$('#newPassword1').val('');
					$('#newPassword2').val('');
				}
		  }
		});
	}
	else
	{
		$('#changePasswordMessage').text("New passwords don't match.");
	}
}