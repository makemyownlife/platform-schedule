<!DOCTYPE html>
<html>
<head>
  	<#import "/common/common.macro.ftl" as netCommon>
	<@netCommon.commonStyle />
    <!-- DataTables -->
    <link rel="stylesheet" href="${request.contextPath}/static/adminlte/plugins/datatables/dataTables.bootstrap.css">
    <title>任务调度系统--在线应用实例</title>
</head>
<body class="hold-transition skin-purple sidebar-mini <#if cookieMap?exists && "off" == cookieMap["xxljob_adminlte_settings"].value >sidebar-collapse</#if>">
<div class="wrapper">
    <!-- header -->
	<@netCommon.commonHeader />
    <!-- left -->
	<@netCommon.commonLeft "onlineapp" />

    <!-- Content Wrapper. Contains page content -->
    <div class="content-wrapper">
        <!-- Content Header (Page header) -->
        <section class="content-header">
        </section>

        <!-- Main content -->
        <section class="content">
            <div class="row">
                <div class="col-xs-4">
                    <div class="input-group">
                        <span class="input-group-addon">选择调度服务器</span>
                        <select class="form-control" id="namesrvIp" name="namesrvIp">
                         <#list platformNamesrvList as platformNamesrv>
                                <option value="${platformNamesrv.namesrvIp}" <#if 0==platformNamesrv.role>selected</#if> >${platformNamesrv.namesrvIp}</option>
                            </#list>
                        </select>
                    </div>
                </div>
                <!--
                <div class="col-xs-3">
                    <div class="input-group">
                        <span class="input-group-addon">应用名称</span>
                        <input type="text" class="form-control" id="appName" autocomplete="off">
                    </div>
                </div>
                -->
                <div class="col-xs-1">
                    <button class="btn btn-block btn-info" id="searchBtn">查询</button>
                </div>
            </div>

            <div class="row" style="margin-top: 5px;">
                <div class="col-xs-12">
                    <div class="box">
                        <div class="box-body">
                            <table id="onlineListTable" class="table table-bordered table-striped" width="100%">
                                <thead>
                                <tr>
                                    <th name="appName">应用名</th>
                                    <th name="clientId">clientId</th>
                                    <th name="createTime">链接时间</th>
                                    <th name="updateTime">最后心跳时间</th>
                                </tr>
                                </thead>
                                <tbody></tbody>
                                <tfoot></tfoot>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </section>
    </div>

    <!-- footer -->
	<@netCommon.commonFooter />
</div>

<@netCommon.commonScript />
<!-- DataTables -->
<script src="${request.contextPath}/static/adminlte/plugins/datatables/jquery.dataTables.min.js"></script>
<script src="${request.contextPath}/static/adminlte/plugins/datatables/dataTables.bootstrap.min.js"></script>
<script src="${request.contextPath}/static/plugins/jquery/jquery.validate.min.js"></script>
<!-- moment -->
<script src="${request.contextPath}/static/adminlte/plugins/daterangepicker/moment.min.js"></script>

<!-- 弹窗 -->
<script src="${request.contextPath}/static/plugins/layer/layer.js?t=20181226"></script>

<!-- custom -->
<script src="${request.contextPath}/static/js/onlineapp.index.1.js?t=2019n71"></script>

</body>
</html>
