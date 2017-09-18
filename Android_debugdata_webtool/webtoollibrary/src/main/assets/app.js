var rootUrl = "";
var dbFileName;
var SPFileName;
var downloadFilePath;
var downloadFilePath2;
$(document).ready(function () {
	getDBList();
	$("#query").keypress(function (e) {
		if (e.which == 13) {
			queryFunction(dbFileName);
		}
	});
	$("#dbwindow").show();
	$("#spwindow").hide();
	$("#fmwindow").hide();

	$("#btndb").click(function () {
		$("#dbwindow").show();
		$("#spwindow").hide();
		$("#fmwindow").hide();
		getDBList();
	});
	$("#btnsp").click(function () {
		$("#dbwindow").hide();
		$("#spwindow").show();
		$("#fmwindow").hide();
		getSpList();
	});
	$("#btnfm").click(function () {
		$("#dbwindow").hide();
		$("#spwindow").hide();
		$("#fmwindow").show();
		getFileList();
	});


	//update currently selected database
	$(document).on("click", "#db-list .list-group-item", function () {
		$("#db-list .list-group-item").each(function () {
			$(this).removeClass('selected');
		});
		$(this).addClass('selected');
	});
	$(document).on("click", "#btnAll>div", function () {
		$("#pane_show>div").each(function () {
			$(this).hide();
		});
		$("#pane_show>div");
	});
	$(document).on("click", "#db-list .list-group-item", function () {
		$("#db-list .list-group-item").each(function () {
			$(this).removeClass('selected');
		});
		$(this).addClass('selected');
	});
	$(document).on("click", "#db-list .list-group-item", function () {
		$("#db-list .list-group-item").each(function () {
			$(this).removeClass('selected');
		});
		$(this).addClass('selected');
	});

	//update currently table database
	$(document).on("click", "#table-list .list-group-item", function () {
		$("#table-list .list-group-item").each(function () {
			$(this).removeClass('selected');
		});
		$(this).addClass('selected');
	});
});

var isDatabaseSelected = true;

function getData(fileName, tableNameOrPath, isDB) {
	var getData
	if (isDB == "true") {
		getData = {
			action: "getDataFromDbTable",
			database: fileName,
			tableName: tableNameOrPath,
		}
	} else {
		SPFileName = fileName;
		downloadFilePath2 = tableNameOrPath;
		getData = {
			action: "getDataFromSpFile",
			SPFileName: fileName
		}
	}

	$.ajax({
		type: "POST",
		url: rootUrl,
		crossDomain: true,
		data: JSON.stringify(getData),
		contentType: "application/json; charset=utf-8",
		dataType: "json",
		success: function (result) {
			inflateData(result, isDB == "true");
		}
	});

}

function queryFunction(dbname) {
	var query = $('#query').val();
	$.ajax({
		type: "POST",
		crossDomain: true,
		url: rootUrl,
		data: JSON.stringify({
			action: "query",
			database: dbname,
			data: query
		}),
		contentType: "application/json; charset=utf-8",
		dataType: "json",
		success: function (result) {
			inflateData(result, true);
		}
	});
}

function downloadFile(path) {
	if (isDatabaseSelected) {
//		$.ajax({
//			url: "downloadFile?downloadFile="+path, success: function () {
		window.location = "downloadFile?downloadFile=" + path;
//			}
//		});
	}
}


function getDBList() {
	$.ajax({
		type: "POST",
		url: rootUrl,
		crossDomain: true,
		data: JSON.stringify({action: "getDbList"}),
		contentType: "application/json; charset=utf-8",
		dataType: "json",
		success: function (result) {
			if (result.code == 200) {
				var dbList = result.dbList;
				$('#db-list').empty();
				var isSelectionDone = false;
				for (var count = 0; count < dbList.length; count++) {
					$("#db-list").append("<a href='#' id=" + dbList[count].fileName + " class='list-group-item' onClick='openDatabaseAndGetTableList(\"" + dbList[count].fileName + "\",\"" + dbList[count].path + "\")'>" + dbList[count].fileName + "</a>");
				}
				if (!isSelectionDone) {
					isSelectionDone = true;
					$('#db-list').find('a').trigger('click');
				}
			}
		}
	});
}

function getSpList() {
	$.ajax({
		type: "POST",
		url: rootUrl,
		crossDomain: true,
		data: JSON.stringify({action: "getSpList"}),
		contentType: "application/json; charset=utf-8",
		dataType: "json",
		success: function (result) {
			if (result.code == 200) {
				var spList = result.spList;
				$('#sp-list').empty();
				var isSelectionDone = false;
				for (var count = 0; count < spList.length; count++) {
					$("#sp-list").append("<a href='#' id=" + spList[count].fileName + " class='list-group-item' onClick='getData(\"" + spList[count].fileName + "\",\"" + spList[count].path + "\",\"" + false + "\")'>" + spList[count].fileName + "</a>");
				}
				if (!isSelectionDone) {
					isSelectionDone = true;
					$('#sp-list').find('a').trigger('click');
				}
			}
		}
	});
}
function getFileList() {
	$.ajax({
		type: "POST",
		url: rootUrl,
		crossDomain: true,
		data: JSON.stringify({action: "getFileList"}),
		contentType: "application/json; charset=utf-8",
		dataType: "json",
		success: function (result) {
			if (result.code == 200) {
				var fileList = result.fileList;
				var columnHeader = [{"data": "fileName"}, {"data": "fileSize"}, {"data": "fileTime"}, {"data": "dir"}];
				var columnData = result.fileList;

				var tableId = "#fm-data";
				// if ($.fn.DataTable.isDataTable(tableId)) {
				// 	$(tableId).DataTable().destroy();
				// }
				$("#fm-data-div").remove();
				var thead="<thead><tr><th>文件名</th><th>文件大小</th><th>最后编辑时间</th><th>是否是文件夹</th></tr></thead>"
				$("#parent-data-divfm").append('<div id="fm-data-div"><table class="display nowrap" cellpadding="0" border="0" cellspacing="0" width="100%" class="table table-striped table-bordered display" id="fm-data">'+thead+'</table></div>');
				$(tableId).dataTable(
						{
							columns: columnHeader,
							data: columnData
						}
				)
			} else {
				showErrorInfo(result.msg);
			}
		}
	});
}
function openDatabaseAndGetTableList(dbname, path) {
	dbFileName = dbname;
	downloadFilePath = path;
	if ("APP_SHARED_PREFERENCES" == dbname) {
		$('#run-query').removeClass('active');
		$('#run-query').addClass('disabled');
		$('#selected-db-info').removeClass('active');
		$('#selected-db-info').addClass('disabled');
		isDatabaseSelected = false;
		$("#selected-db-info").text("SharedPreferences");
	} else {
		$('#run-query').removeClass('disabled');
		$('#run-query').addClass('active');
		$('#selected-db-info').removeClass('disabled');
		$('#selected-db-info').addClass('active');
		isDatabaseSelected = true;
		$("#selected-db-info").text("点击数据库名称下载 : " + dbname);
	}
	$.ajax({
		type: "POST",
		crossDomain: true,
		url: rootUrl,
		data: JSON.stringify({
			action: "getTableList",
			database: dbname
		}),
		contentType: "application/json; charset=utf-8",
		dataType: "json",
		success: function (result) {
			if (result.code == 200) {
				var tableList = result.tableList;
				var dbVersion = result.dbVersion;
				$("#selected-db-info").text("点击数据库名称下载 : " + dbname + " Version : " + dbVersion);
				$("#selected-db-info").click(function () {
					downloadFile(downloadFilePath)
				});
				$('#table-list').empty()
				for (var count = 0; count < tableList.length; count++) {
					$("#table-list").append("<a href='#' data-db-name='" + dbname + "' data-table-name='" + tableList[count] + "' class='list-group-item' onClick='getData(\"" + dbname + "\",\"" + tableList[count] + "\",\"" + true + "\");'>" + tableList[count] + "</a>");
				}
			} else {
				showErrorInfo(result.msg);
			}
		}
	});

}

function inflateData(result, isDB) {

	if (result.code == 200) {
		var columnHeader = result.tableData.tableColumns;
		var columnData = result.tableData.tableDatas;
		for (var i = 0; i < columnHeader.length; i++) {
			columnHeader[i]['targets'] = i;
			columnHeader[i]['data'] = function (data, type, val, meta) {
				return data[meta.col].value;
			}
		}
		var tableId
		if (isDB) {
			tableId = "#db-data";
		} else {
			tableId = "#sp-data";
			$("#selected-sp-info").text("点击文件名称下载 : " + SPFileName);
			$("#selected-sp-info").click(function () {
				downloadFile(downloadFilePath2)
			});
		}
		if ($.fn.DataTable.isDataTable(tableId)) {
			$(tableId).DataTable().destroy();
		}

		if (isDB) {
			$("#db-data-div").remove();
			$("#parent-data-div").append('<div id="db-data-div"><table class="display nowrap" cellpadding="0" border="0" cellspacing="0" width="100%" class="table table-striped table-bordered display" id="db-data"></table></div>');
		} else {
			$("#sp-data-div").remove();
			$("#parent-data-divsp").append('<div id="sp-data-div"><table class="display nowrap" cellpadding="0" border="0" cellspacing="0" width="100%" class="table table-striped table-bordered display" id="sp-data"></table></div>');
		}

		var availableButtons;
		if (result.editable) {
			availableButtons = [
				{
					text: '添加',
					name: 'add' // don not change name
				},
				{
					extend: 'selected', // Bind to Selected row
					text: '编辑',
					name: 'edit'        // do not change name
				},
				{
					extend: 'selected',
					text: '删除',
					name: 'delete'
				}
			];
		} else {
			availableButtons = [];
		}
		var changecolumnData = []
		for (var i = 0; i < columnData.length; i++) {
			changecolumnData[i] = columnData[i].map(function (item, index) {
				return item = {value: item}
			})
		}

		$(tableId).dataTable({
			"data": changecolumnData,
			"columnDefs": columnHeader,
			'bPaginate': true,
			'searching': true,
			'bFilter': true,
			'bInfo': true,
			"bSort": true,
			"scrollX": true,
			"iDisplayLength": 10,
			"dom": "Bfrtip",
			select: 'single',
			altEditor: true,     // Enable altEditor
			buttons: availableButtons
		})

		$(tableId).on('update-row.dt', function (e, updatedRowData, callback) {
			var updatedRowDataArray = JSON.parse(updatedRowData);
			//add value for each column

			if (isDB) {
				var data = columnHeader;
				for (var i = 0; i < data.length; i++) {
					data[i].value = updatedRowDataArray[i].value;
					data[i].dataType = data[i].dataType;
				}
				db_update(data, callback);
			} else {
				sp_update(updatedRowDataArray, callback)
			}

		});

		$(tableId).on('delete-row.dt', function (e, updatedRowData, callback) {
					var deleteRowDataArray = JSON.parse(updatedRowData);
					if (isDB) {
						var data = columnHeader;
						for (var i = 0; i < data.length; i++) {
							data[i].value = deleteRowDataArray[i].value;
							data[i].dataType = deleteRowDataArray[i].dataType;
						}
						db_delete(data, callback);
					} else {
						sp_delete(deleteRowDataArray, callback);
					}
				}
		);

		$(tableId).on('add-row.dt', function (e, updatedRowData, callback) {
			var addRowDataArray = JSON.parse(updatedRowData);
			if (isDB) {
				var data = columnHeader;
				for (var i = 0; i < data.length; i++) {
					data[i].value = addRowDataArray[i].value;
					data[i].dataType = addRowDataArray[i].dataType;
				}
				db_addData(data, callback);
			} else {
				sp_addData(addRowDataArray, callback);
			}
		});

		// hack to fix alignment issue when scrollX is enabled
		$(".dataTables_scrollHeadInner").css({"width": "100%"});
		$(".table ").css({"width": "100%"});
	} else {
		showErrorInfo(result.msg);
	}

}

//send update database request to server
function db_update(updatedData, callback) {
	var selectedTableElement = $("#table-list .list-group-item.selected");
	var filteredUpdatedData = updatedData.map(function (columnData) {
		return {
			title: columnData.title,
			isPrimary: columnData.primary,
			value: columnData.value,
			dataType: columnData.dataType
		}
	});
	var requestParameters = {};
	requestParameters.action = "updateDataToDb"
	requestParameters.database = selectedTableElement.attr('data-db-name');
	requestParameters.tableName = selectedTableElement.attr('data-table-name');
	requestParameters.RowDataRequests = filteredUpdatedData
	$.ajax({
		url: rootUrl,
		type: 'POST',
		data: JSON.stringify(requestParameters),
		success: function (response) {
			if (response.code == 200) {
				callback(true);
				showSuccessInfo("数据更新成功");
				getData(requestParameters.database + "," + requestParameters.tableName);
			} else {
				showErrorInfo(response.msg)
				callback(false);
			}
		}
	})
}
function sp_update(updatedData, callback) {
	var requestParameters = {};
	requestParameters.action = "updateDataToSp"
	requestParameters.spFileName = SPFileName;
	requestParameters.RowDataRequests = updatedData;
	$.ajax({
		url: rootUrl,
		type: 'POST',
		data: JSON.stringify(requestParameters),
		success: function (response) {
			if (response.code == 200) {
				callback(true);
				showSuccessInfo("数据更新成功");
				getData(requestParameters.spFileName, "", false);
			} else {
				showErrorInfo(response.msg)
				callback(false);
			}
		}
	})
}
function db_delete(deleteData, callback) {
	var selectedTableElement = $("#table-list .list-group-item.selected");
	var filteredUpdatedData = deleteData.map(function (columnData) {
		return {
			title: columnData.title,
			isPrimary: columnData.primary,
			value: columnData.value,
			dataType: columnData.dataType
		}
	});
	var requestParameters = {};
	requestParameters.action = "deleteDataFromDb"
	requestParameters.database = selectedTableElement.attr('data-db-name');
	requestParameters.tableName = selectedTableElement.attr('data-table-name');
	requestParameters.RowDataRequests = filteredUpdatedData
	$.ajax({
		url: rootUrl,
		type: 'POST',
		data: JSON.stringify(requestParameters),
		success: function (response) {
			if (response.code == 200) {
				callback(true);
				getData(requestParameters.database + "," + requestParameters.tableName);
				showSuccessInfo("数据删除成功");
			} else {
				showErrorInfo(response.msg)
				callback(false);
			}
		}
	})
}
function sp_delete(updatedData, callback) {
	var requestParameters = {};
	requestParameters.action = "deleteDataFromSp"
	requestParameters.spFileName = SPFileName;
	requestParameters.RowDataRequests = updatedData;
	$.ajax({
		url: rootUrl,
		type: 'POST',
		data: JSON.stringify(requestParameters),
		success: function (response) {
			if (response.code == 200) {
				callback(true);
				showSuccessInfo("数据删除成功");
				getData(requestParameters.spFileName, "", false);
			} else {
				showErrorInfo(response.msg)
				callback(false);
			}
		}
	})
}
function db_addData(deleteData, callback) {
	var selectedTableElement = $("#table-list .list-group-item.selected");
	var filteredUpdatedData = deleteData.map(function (columnData) {
		return {
			title: columnData.title,
			isPrimary: columnData.primary,
			value: columnData.value,
			dataType: columnData.dataType
		}
	});
	var requestParameters = {
		"action": "addDataToDb",
		"database": "",
		"tableName": "",
	};
	requestParameters.database = selectedTableElement.attr('data-db-name');
	requestParameters.tableName = selectedTableElement.attr('data-table-name');
	requestParameters.RowDataRequests = filteredUpdatedData
	$.ajax({
		url: rootUrl,
		type: 'POST',
		data: JSON.stringify(requestParameters),
		success: function (response) {
			if (response.code == 200) {
				callback(true);
				getData(requestParameters.database + "," + requestParameters.tableName);
				showSuccessInfo("数据添加成功");
			} else {
				showErrorInfo(response.msg)

				callback(false);
			}
		}
	});
}
function sp_addData(updatedData, callback) {
	var requestParameters = {};
	requestParameters.action = "addDataToSp"
	requestParameters.spFileName = SPFileName;
	requestParameters.RowDataRequests = updatedData;
	$.ajax({
		url: rootUrl,
		type: 'POST',
		data: JSON.stringify(requestParameters),
		success: function (response) {
			if (response.code == 200) {
				callback(true);
				showSuccessInfo("数据添加成功");
				getData(requestParameters.spFileName, "", false);
			} else {
				showErrorInfo(response.msg)
				callback(false);
			}
		}
	})
}
function showSuccessInfo(message) {
	var snackbarId = "snackbar";
	var snackbarElement = $("#" + snackbarId);
	snackbarElement.addClass("show");
	snackbarElement.css({"backgroundColor": "#5cb85c"});
	snackbarElement.html(message)
	setTimeout(function () {
		snackbarElement.removeClass("show");
	}, 3000);
}

function showErrorInfo(message) {
	var snackbarId = "snackbar";
	var snackbarElement = $("#" + snackbarId);
	snackbarElement.addClass("show");
	snackbarElement.css({"backgroundColor": "#d9534f"});
	snackbarElement.html('')
	snackbarElement.html(message)
	setTimeout(function () {
		snackbarElement.removeClass("show");
	}, 3000);
}
