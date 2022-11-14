$(function () {

    //init date tables
    var appListTable = $("#clusterListTable").dataTable({
        "deferRender": true,
        "processing": true,
        "serverSide": true,
        "ajax": {
            url: timestamp(base_url + "/cluster/pageList"),
            type: "post",
            data: function (d) {
                var obj = {};
                obj.start = d.start;
                obj.length = d.length;
                return obj;
            }
        },
        "searching": false,
        "ordering": false,
        //"scrollX": true,	// scroll x，close self-adaption
        "columns": [
            {
                "data": 'id',
                "bSortable": false,
                "visible": true,
                "width": '8%'
            },
            {
                "data": 'namesrvIp',
                "visible": true,
                "width": '20%',
                "render": function (data, type, row) {
                    return data;
                }
            },
            {
                "data": 'status',
                "visible": true,
                "width": '8%',
                "render": function (data, type, row) {
                    var html = data;
                    if (data == 0) {
                        html = '<span style="color: green">' + '正常' + '</span>';
                    } else if (data == 1) {
                        html = '<span style="color: grey">' + '失效' + '</span>';
                    } else {
                        html = '';
                    }
                    return html;
                }
            },
            {
                "data": 'role',
                "visible": true,
                "width": '15%',
                "render": function (data, type, row) {
                    var html = data;
                    if (data == 0) {
                        html = '<span style="color: green">' + 'master' + '</span>';
                    } else if (data == 1) {
                        html = '<span style="color: grey">' + 'slave' + '</span>';
                    } else {
                        html = '';
                    }
                    return html;
                }
            },
            {
                "width": '13%',
                "data": 'createTime',
                "visible": true,
                "render": function (data, type, row) {
                    return data ? moment(new Date(data)).format("YYYY-MM-DD HH:mm:ss") : "";
                }
            },
            {
                "width": '13%',
                "data": 'updateTime',
                "visible": true,
                "render": function (data, type, row) {
                    return data ? moment(new Date(data)).format("YYYY-MM-DD HH:mm:ss") : "";
                }
            },
            {
                "data": 'id',
                "visible": true,
                "render": function (data, type, row) {
                    var btn = '<button class="btn btn-warning btn-xs" type="button" onclick="toUpdateNamesrvPage(' + data + ')">编辑</button> ';
                    var tipBtn = '<button class="btn btn-danger btn-xs" type="button" onclick="deleteNamesrv(' + data + ')">删除</button> ';
                    return btn + tipBtn;
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

    toUpdateNamesrvPage = function (id) {
        layer.open({
            type: 2,
            title: '编辑集群',
            maxmin: true,
            shadeClose: false, //点击遮罩关闭层
            area: ['400px', '350px'],
            content: base_url + '/cluster/updatepage?id=' + id
        });
    };

    toAddNamesrvPage = function () {
        layer.open({
            type: 2,
            title: '添加集群',
            maxmin: true,
            shadeClose: false, //点击遮罩关闭层
            area: ['400px', '350px'],
            content: base_url + '/cluster/addpage'
        });
    }

    deleteNamesrv = function (id) {
        layer.confirm('您确定删除该集群吗?', {btn: ['确定', '取消'], title: "提示"}, function () {
            $.post(base_url + "/cluster/delete", {
                id: id,
                t: new Date().getTime()
            }, function (data, status) {
                if (data.code == "200") {
                    layer.open({
                        title: '系统提示',
                        btn: ['确定'],
                        content: '删除成功',
                        icon: '1',
                        end: function (layero, index) {
                            window.parent.location.reload();
                        }
                    });
                } else {
                    layer.open({
                        title: '系统提示',
                        btn: ['确定'],
                        content: '删除失败',
                        icon: '2'
                    });
                }
            });
        });
    }



});