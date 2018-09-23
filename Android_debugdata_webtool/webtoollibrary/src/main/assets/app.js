var rootUrl = "http://192.168.0.106:8088";
var dbFileName;
var currentTableName;
var SPFileName;
var downloadFilePath1;
var downloadFilePath2;
var FilePath3;
$(document).ready(function () {
    getDBList();
    $("#query").keypress(function (e) {
        if (e.which == 13) {
            queryFunction();
        }
    });
    $("#dbwindow").show();
    $("#spwindow").hide();
    $("#fmwindow").hide();

    $("#btndb").click(function () {
        $("#dbwindow").show();
        $("#spwindow").hide();
        $("#fmwindow").hide();
    });
    $("#btnsp").click(function () {
        $("#dbwindow").hide();
        $("#spwindow").show();
        $("#fmwindow").hide();
        if (downloadFilePath2 == null) {
            getSpList();
        }
    });
    $("#btnfm").click(function () {
        $("#dbwindow").hide();
        $("#spwindow").hide();
        $("#fmwindow").show();
        if (FilePath3 == null) {
            getFileList();
        }
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
    if (isDB == "true") {
    	getDataFromDb(fileName,tableNameOrPath);
    } else {
    	getDataFromSp(fileName,tableNameOrPath);
    }
}

function getDataFromDb(fileName, dbtableName) {
    var getData
    	currentTableName=dbtableName;
        getData = {
            action: "getDataFromDbTable",
            database: fileName,
            tableName: dbtableName,
            pageIndex: 1,
            pageSize:10
        }
    $.ajax({
        type: "POST",
        url: rootUrl, 
        crossDomain: true,
        data: JSON.stringify(getData),
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        success: function (result) {
            inflateDataFromDb(result);
        }
    });
}

function getDataFromSp(fileName, tableNameOrPath) {
    var getData
	SPFileName = fileName;
	downloadFilePath2 = tableNameOrPath;
	getData = {
		action: "getDataFromSpFile",
		SPFileName: fileName
	}

    $.ajax({
        type: "POST",
        url: rootUrl, 
        crossDomain: true,
        data: JSON.stringify(getData),
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        success: function (result) {
            inflateDataFromSp(result);
        }
    });
}


function inflateDataFromDb(result) {
    if (result.code == 200) {
        var columnHeader = result.tableData.tableColumns;
        var columnData = result.tableData.tableDatas;
        for (var i = 0; i < columnHeader.length; i++) {
            columnHeader[i]['targets'] = i;
            columnHeader[i]['data'] = function (data, type, val, meta) {
                return data[meta.col].value;
            }
        }
        var tableId = "#db-data";
		if ($.fn.DataTable.isDataTable(tableId)) {
			$(tableId).DataTable().destroy();
        }

		$("#db-data-div").remove();
		$("#parent-data-divdb").append('<div id="db-data-div"><table class="display nowrap" cellpadding="0" border="0" cellspacing="0" width="100%" class="table table-striped table-bordered display" id="db-data"></table></div>');

        var availableButtons = [];
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
        }
        
        // console.info(columnHeader);
        $(tableId).dataTable({
        	"columnDefs": columnHeader,
        	"processing": true,
        	"serverSide": true,
        	ajax: function (data, callback, settings) {
				 //封装请求参数
				var param = {};
				param.pageSize = data.length;//页面显示记录条数，在页面显示每页显示多少项的时候
				param.pageIndex = (data.start / data.length)+1;//开始的记录序号
				param.database = dbFileName;
				param.tableName = currentTableName;
				param.action = "getDataFromDbTable";
				param.draw = data.draw;
				 //console.log(param);
				 //ajax请求数据
				 $.ajax({
					type: "POST",
					url: rootUrl,
					data: JSON.stringify(param), //传入组装的参数
					dataType: "json",
					contentType: 'application/json; charset=utf-8',
					success: function (result) {
					  // console.log(result);
						  //封装返回数据
						var returnData = {};
						returnData.draw = param.draw;//这里直接自行返回了draw计数器,应该由后台返回
						returnData.recordsTotal = result.tableData.dataCount;//返回数据全部记录
						returnData.recordsFiltered = result.tableData.dataCount;//后台不实现过滤功能，每次查询均视作全部结果
						returnData.data = result.tableData.tableDatas;//返回的数据列表
						for (var i = 0; i < result.tableData.tableDatas.length; i++) {
							returnData.data[i] = result.tableData.tableDatas[i].map(function (item, index) {
								return item = {value: item, dataType: columnHeader[index].dataType}
							})
						}
						  // console.log(returnData);
						  //调用DataTables提供的callback方法，代表数据已封装完成并传回DataTables进行渲染
						  //此时的数据需确保正确无误，异常判断应在执行此回调前自行处理完毕
						callback(returnData);
					}
				});
			},
            language: {
                url: rootUrl+'/language/Chinese.json'
            },
            select: 'single',
            altEditor: true,     // Enable altEditor
            "dom": "Bfrtip",
            buttons: availableButtons,
        })

        $(tableId).on('update-row.dt', function (e, updatedRowData, callback) {
			var updatedRowDataArray = JSON.parse(updatedRowData);
			var data = columnHeader;
			for (var i = 0; i < data.length; i++) {
				data[i].value = updatedRowDataArray[i].value;
				data[i].dataType = updatedRowDataArray[i].dataType;
			}
			db_update(data, callback);
        });

        $(tableId).on('delete-row.dt', function (e, deleteRowData, callback) {
			var deleteRowDataArray = JSON.parse(deleteRowData);
			var data = columnHeader;
			for (var i = 0; i < data.length; i++) {
				data[i].value = deleteRowDataArray[i].value;
				data[i].dataType = deleteRowDataArray[i].dataType;
			}
			db_delete(data, callback);
		});

        $(tableId).on('add-row.dt', function (e, addRowData, callback) {
			var addRowDataArray = JSON.parse(addRowData);
			var data = columnHeader;
			for (var i = 0; i < data.length; i++) {
				data[i].value = addRowDataArray[i].value;
				data[i].dataType = addRowDataArray[i].dataType;
			}
			db_addData(data, callback);
		});

        // hack to fix alignment issue when scrollX is enabled
        $(".dataTables_scrollHeadInner").css({"width": "100%"});
        $(".table ").css({"width": "100%"});
    } else {
        showErrorInfo(result.msg);
    }

}
// 读取共享参数文件数据不分页，相对数据库来说，存储量级低很多
function inflateDataFromSp(result) {
    if (result.code == 200) {
        var columnHeader = result.tableData.tableColumns;
        var columnData = result.tableData.tableDatas;
        for (var i = 0; i < columnHeader.length; i++) {
            columnHeader[i]['targets'] = i;
            columnHeader[i]['data'] = function (data, type, val, meta) {
                return data[meta.col].value;
            }
        }

        var tableId = "#sp-data";
		$("#selected-sp-info").text("点击文件名称下载 : " + SPFileName);
		$("#selected-sp-info").click(function () {
			downloadFile(downloadFilePath2)
		});
		if(document.getElementById("id")){
			if ($.fn.DataTable.isDataTable(tableId)) {
				$(tableId).DataTable().destroy();
			}
		}

		$("#sp-data-div").remove();
		$("#parent-data-divsp").append('<div id="sp-data-div"><table class="display nowrap" cellpadding="0" border="0" cellspacing="0" width="100%" class="table table-striped table-bordered display" id="sp-data"></table></div>');

        var availableButtons = [];
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
        }
		var changecolumnData = []
		for (var i = 0; i < columnData.length; i++) {
			changecolumnData[i] = columnData[i].map(function (item, index) {
				return item = {value: item, dataType: columnHeader[index].dataType}
			})
        }
        $(tableId).dataTable({
        	"columnDefs": columnHeader,
			"data": changecolumnData,
            language: {
                url: rootUrl+'/language/Chinese.json'
            },
            select: 'single',
            altEditor: true,     // Enable altEditor
            "dom": "Bfrtip",
            buttons: availableButtons,
        })

        $(tableId).on('update-row.dt', function (e, updatedRowData, callback) {
			var updatedRowDataArray = JSON.parse(updatedRowData);
			sp_update(updatedRowDataArray, callback)

        });

		$(tableId).on('delete-row.dt', function (e, deleteRowData, callback) {
			var deleteRowDataArray = JSON.parse(deleteRowData);
			sp_delete(deleteRowDataArray, callback);
			}
		);

		$(tableId).on('add-row.dt', function (e, addRowData, callback) {
			var addRowDataArray = JSON.parse(addRowData);
			sp_addData(addRowDataArray, callback);
		});

        // hack to fix alignment issue when scrollX is enabled
        $(".dataTables_scrollHeadInner").css({"width": "100%"});
        $(".table ").css({"width": "100%"});
    }

    else {
        showErrorInfo(result.msg);
    }

}
// 从数据库拿数据局，不分页
function inflateDataFromDb2(result) {
    if (result.code == 200) {
        var columnHeader = result.tableData.tableColumns;
        var columnData = result.tableData.tableDatas;
        for (var i = 0; i < columnHeader.length; i++) {
            columnHeader[i]['targets'] = i;
            columnHeader[i]['data'] = function (data, type, val, meta) {
                return data[meta.col].value;
            }
        }
        var tableId = "#db-data";
		if ($.fn.DataTable.isDataTable(tableId)) {
			$(tableId).DataTable().destroy();
        }

		$("#db-data-div").remove();
		$("#parent-data-divdb").append('<div id="db-data-div"><table class="display nowrap" cellpadding="0" border="0" cellspacing="0" width="100%" class="table table-striped table-bordered display" id="db-data"></table></div>');

        var availableButtons = [];
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
        }
        
        var changecolumnData = []
		for (var i = 0; i < columnData.length; i++) {
			changecolumnData[i] = columnData[i].map(function (item, index) {
				return item = {value: item, dataType: columnHeader[index].dataType}
			})
        }
        $(tableId).dataTable({
        	"columnDefs": columnHeader,
			"data": changecolumnData,
            language: {
                url: rootUrl+'/language/Chinese.json'
            },
            select: 'single',
            altEditor: true,     // Enable altEditor
            "dom": "Bfrtip",
            buttons: availableButtons,
        })

        $(tableId).on('update-row.dt', function (e, updatedRowData, callback) {
			var updatedRowDataArray = JSON.parse(updatedRowData);
			var data = columnHeader;
			for (var i = 0; i < data.length; i++) {
				data[i].value = updatedRowDataArray[i].value;
				data[i].dataType = updatedRowDataArray[i].dataType;
			}
			db_update(data, callback);
        });

        $(tableId).on('delete-row.dt', function (e, deleteRowData, callback) {
			var deleteRowDataArray = JSON.parse(deleteRowData);
			var data = columnHeader;
			for (var i = 0; i < data.length; i++) {
				data[i].value = deleteRowDataArray[i].value;
				data[i].dataType = deleteRowDataArray[i].dataType;
			}
			db_delete(data, callback);
		});

        $(tableId).on('add-row.dt', function (e, addRowData, callback) {
			var addRowDataArray = JSON.parse(addRowData);
			var data = columnHeader;
			for (var i = 0; i < data.length; i++) {
				console.info(addRowDataArray[i])
				data[i].value = addRowDataArray[i].value;
				data[i].dataType = addRowDataArray[i].dataType;
			}
			db_addData(data, callback);
		});

        // hack to fix alignment issue when scrollX is enabled
        $(".dataTables_scrollHeadInner").css({"width": "100%"});
        $(".table ").css({"width": "100%"});
    } else {
        showErrorInfo(result.msg);
    }

}

function queryFunction() {
    var query = $('#query').val();
    $.ajax({
        type: "POST",
        crossDomain: true,
        url: rootUrl,
        data: JSON.stringify({
            action: "query",
            database: dbFileName,
            data: query
        }),
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        success: function (result) {
            inflateDataFromDb2(result);
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


var FilecolumnData;
var table;

function getFileList(path) {
    var tableId = "#fm-data";
    if (FilecolumnData == null) {
        $("#fm-data-div").remove();
        $("#parent-data-divfm").append('<div id="fm-data-div"><table class="display nowrap" cellpadding="0" border="0" cellspacing="0" width="100%" class="table table-striped table-bordered display" id="fm-data">' + '</table></div>');
        $(tableId).removeClass('display').addClass('table table-striped table-bordered');
    }
    $.ajax({
        type: "POST",
        url: rootUrl,
        crossDomain: true,
        data: JSON.stringify(path == null ? {action: "getFileList"} : {
            action: "getFileList",
            "data": path
        }),
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        success:
            function (result) {
                if (result.code == 200) {
                    FilecolumnData = result.fileList.fileList;
                    for (i = 0; i < FilecolumnData.length; i++) {
                        if (i == 0) {
                            FilePath3 = FilecolumnData[0].rootPath;
                        }
                        if (FilecolumnData[i].dir == true) {
                            FilecolumnData[i].fileName = "<div onClick='getFileList(\"" + FilecolumnData[i].path + "\")'> <img src=\"images/folder.png\"/> <a>" + FilecolumnData[i].fileName + "</a></div>";
                            FilecolumnData[i].delete = "";
                        } else {
                            FilecolumnData[i].fileName = "<div  onClick='downloadFile(\"" + FilecolumnData[i].path + "\")'> <img src=\"images/file.png\"/><a>" + FilecolumnData[i].fileName + "</a></div>";
                            FilecolumnData[i].delete = "<div><button  onClick='file_delete(\"" + i + "\",\"" + FilecolumnData[i].path + "\")'>删除</button></div>";
                        }
                    }


                    if ($.fn.DataTable.isDataTable(tableId)) {
                        $(tableId).DataTable().destroy();
                    }
                    table = $(tableId).DataTable(
                        {
                            columns: result.fileList.fileColumns,
                            data: FilecolumnData,
                            language: {
                                url: '/language/Chinese.json'
                            },
                            select: 'single',
                            altEditor: true,
                            dom: 'Bfrtip',
                            buttons: [
                                {
                                    text: '返回上级目录',
                                    action: function (e, dt, node, config) {
                                        getFileList(FilePath3);
                                    }
                                }
                            ]
                        }
                    );
                    // table.on('click', 'tr', function () {
                    //     var data = table.row(this).data();
                    //     alert('You clicked on ' + data.fileName + '\'s row');
                    // });
                } else {
                    showErrorInfo(result.msg);
                }
            }
    });
}

function file_delete(position, path) {
    $.ajax({
        type: "POST",
        url: rootUrl,
        crossDomain: true,
        data: JSON.stringify({
            action: "deleteFile",
            "data": path
        }),
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        success:
            function (result) {
                if (result.code == 200) {
                    table.rows('.selected')
                        .remove()
                        .draw();
                    showSuccessInfo(result.msg);
                } else {
                    showErrorInfo(result.msg);
                }
            }
    });
}

function openDatabaseAndGetTableList(dbname, path) {
    dbFileName = dbname;
    downloadFilePath1 = path;
    $('#run-query').removeClass('disabled');
    $('#run-query').addClass('active');
    $('#selected-db-info').removeClass('disabled');
    $('#selected-db-info').addClass('active');
    isDatabaseSelected = true;
    $("#selected-db-info").text("点击数据库名称下载 : " + dbname);
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
                    downloadFile(downloadFilePath1)
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
