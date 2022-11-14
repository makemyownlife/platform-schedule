$(function () {

//init date tables
    var joblogTable = $("#joblogTable").dataTable({
        "deferRender": true,
        "processing": true,
        "serverSide": true,
        "ajax": {
            url: timestamp(base_url + "/joblog/pageList"),
            type: "post",
            data: function (d) {
                var obj = {};
                obj.logStatus = $('#logStatus').val();
                obj.start = d.start;
                obj.length = d.length;
                obj.jobId = $('#jobSelect').val();
                return obj;
            }
        },
        "searching": false,
        "ordering": false,
        //"scrollX": true,	// scroll x，close self-adaption
        "columns": [
            {
                "data": 'jobId',
                "visible": true,
                "width": '9%',
                "render": function (data, type, row) {
                    return data;
                }
            },
            {
                "data": 'triggerTime',
                "visible": true,
                "width": '14%',
                "render": function (data, type, row) {
                    return data;
                }
            },
            {
                "data": 'triggerStatus',
                "visible": true,
                "width": '10%',
                "render": function (data, type, row) {
                    var html = data;
                    if (data == 0) {
                        html = '<span style="color: green">' + '成功' + '</span>';
                    } else if (data == 1) {
                        html = '<span style="color: red">' + '失败' + '</span>';
                    } else {
                        html = '';
                    }
                    return html;
                }
            },
            {
                "data": 'callbackTime',
                "visible": true,
                "width": '14%',
                "render": function (data, type, row) {
                    return data;
                }
            },
            {
                "data": 'callbackStatus',
                "visible": true,
                "width": '10%',
                "render": function (data, type, row) {
                    var html = data;
                    if (data == 0) {
                        html = '<span style="color: green">' + '成功' + '</span>';
                    } else if (data == 1) {
                        html = '<span style="color: red">' + '失败' + '</span>';
                    } else {
                        html = '';
                    }
                    return html;
                }
            },
            {
                "data": 'message',
                "visible": true,
                "render": function (data, type, row) {
                    var btn = '<button class="btn btn-warning btn-xs" _triggerMessage="' + row.triggerMessage + '"' +  ' _callbackMessage="' + row.callbackMessage +  '" type="button" onclick="logMessage(this)">查看</button> ';
                    return btn;
                }
            }
        ],
        "language": {
            "sProcessing": '\u5904\u7406\u4E2D...',
            "sLengthMenu": '\u6BCF\u9875 _MENU_ \u6761\u8BB0\u5F55',
            "sZeroRecords": '\u6CA1\u6709\u5339\u914D\u7ED3\u679C',
            "sInfo": '\u7B2C _PAGE_ \u9875 ( \u603B\u5171 _PAGES_ \u9875\uFF0C_TOTAL_ \u6761\u8BB0\u5F55 )',
            "sInfoEmpty": '\u65E0\u8BB0\u5F55',
            "sInfoFiltered": '',
            "sInfoPostFix": "",
            "sSearch": "\u641C\u7D22",
            "sUrl": "",
            "sEmptyTable": "",
            "sLoadingRecords": '\u8868\u4E2D\u6570\u636E\u4E3A\u7A7A',
            "sInfoThousands": ",",
            "oPaginate": {
                "sFirst": '\u9996\u9875',
                "sPrevious": '\u4E0A\u9875',
                "sNext": '\u4E0B\u9875',
                "sLast": '\u672B\u9875'
            },
            "oAria": {
                "sSortAscending": ': \u4EE5\u5347\u5E8F\u6392\u5217\u6B64\u5217',
                "sSortDescending": ': \u4EE5\u964D\u5E8F\u6392\u5217\u6B64\u5217'
            }
        }
    });

    //点查询按钮触发查询事件
    $('#searchBtn').on('click', function () {
        joblogTable.fnDraw();
    });

    logMessage = function (obj) {
        var current = obj;
        var triggerMessage = obj.getAttribute("_triggerMessage");
        var callbackMessage = obj.getAttribute("_callbackMessage");
        layer.open({
            type: 1,
            skin: 'layui-layer-rim', //加上边框
            area: ['350px', '240px'], //宽高
            content: '触发信息:' + triggerMessage + " <br/>" + "执行信息:" + callbackMessage
        });
    }

});