

var chartResult;
function generateChart(chartType) {
    var finalResult = {};

    if($j("#tree input[type='checkbox']").size() == 0) {
        $j("#linechart").html("Please get build report to generate the chart.")
        return;
    }
    if(reevaluateChartData){
        chartResult = getChartData(getSelectedRows());
        reevaluateChartData = false;
    }
    resetCharts();
    switch (chartType){
        case "all":
            generateLineChart('#linechart','normal');
            generateLineChart('#linepercentage','percent');
            generateBarChart();
            generatePieChart();
            break;
        case "line":
            generateLineChart('#linechart','normal');
            break;
        case "linepercentage":
            generateLineChart('#linepercentage','percent');
            break;
        case "bar":
            generateBarChart();
            break;
        case "pie":
            generatePieChart();
            break;
    }

}

function resetCharts(){
    $j("#linechart").html("");
    $j("#barchart").html("");
    $j("#piechart").html("");
}

function generateLineChart(htmlLocator, lineChartType){
    var chartCategories = [];
    var chartData ={
        "Failed" : [],
        "Passed" : [],
        "Skipped" : [],
        "Total" : []
    };

    for(var key in chartResult) {
        if(chartResult.hasOwnProperty(key)){
            var buildResult = chartResult[key];
            chartCategories.push(key);
            var passed = buildResult["Passed"];
            var failed = buildResult["Failed"];
            var skipped = buildResult["Skipped"];
            var total = buildResult["Total"];
            if(lineChartType == "percent"){
                chartData["Failed"].push(calculateIndividualPercentage(failed, total));
                chartData["Passed"].push(calculateIndividualPercentage(passed, total));
                chartData["Skipped"].push(calculateIndividualPercentage(skipped, total));
                chartData["Total"].push(calculateIndividualPercentage(total, total));
            } else {
                chartData["Failed"].push(failed);
                chartData["Passed"].push(passed);
                chartData["Skipped"].push(skipped);
                chartData["Total"].push(total);
            }
        }
    }
    $j(function () {$j(htmlLocator).highcharts(getLineChartConfig(chartCategories, lineChartType, chartData))});

}

function generateBarChart(){
    var chartCategories = [];
    var chartData ={
        "Failed" : [],
        "Passed" : [],
        "Skipped" : []
    };

    for(var key in chartResult) {
        if(chartResult.hasOwnProperty(key)){
            var buildResult = chartResult[key];
            chartCategories.push(key);
            chartData["Failed"].push(buildResult["Failed"]);
            chartData["Passed"].push(buildResult["Passed"]);
            chartData["Skipped"].push(buildResult["Skipped"]);
        }
    }

    var barChartData = [
        {
            name: "Passed",
            data: chartData["Passed"]
        },
        {
            name: "Failed",
            data: chartData["Failed"]
        },
        {
            name: "Skipped",
            data: chartData["Skipped"]
        },
    ];
    $j(function () {$j("#barchart").highcharts(getBarChartConfig(chartCategories, barChartData))});
}

function generatePieChart(inputData, resultTitle){
    if(inputData == undefined){
        var total = 0;
        var passed = 0;
        var failed = 0;
        var skipped = 0;
        for(var key in chartResult) {
            if(chartResult.hasOwnProperty(key)){
                total++;
                var buildResult = chartResult[key];
                if(buildResult["Failed"]>0){
                    failed ++;
                } else if(buildResult["Passed"]>0) {
                    passed++;
                } else {
                    skipped ++;
                }
                inputData = calculatePercentage(passed,failed,skipped,total);
            }
        }
    }
    $j(function () {$j("#piechart").highcharts(getPieChartConfig(inputData,resultTitle))})
}

function getChartData(selectedRows) {
    var chartResult = {};
    var baseRows;
    if(selectedRows.size()>0) {
        baseRows = selectedRows;
    } else {
        baseRows = $j("[parentclass='base']");
    }

    $j.each(baseRows, function(index, baseRow){
        var buildResults = $j(baseRow).find(".build-result.table-cell");
        $j.each(buildResults, function(index, buildResult){
            var jsonResult = $j.parseJSON($j(buildResult).attr("data-result"));
            var buildNumber = jsonResult["buildNumber"];
            var tempBuildResult = {
                "Failed" :   jsonResult["totalFailed"]?jsonResult["totalFailed"]:0,
                "Skipped" :   jsonResult["totalSkipped"]?jsonResult["totalSkipped"]:0,
                "Passed" :   jsonResult["totalPassed"]?jsonResult["totalPassed"]:0,
                "Total" :   jsonResult["totalTests"]?jsonResult["totalTests"]:0
            };
            if(chartResult[buildNumber]){
                var tempChartBuildResult = chartResult[buildNumber];
                var result = {
                    "Failed": tempChartBuildResult["Failed"] + tempBuildResult["Failed"],
                    "Skipped": tempChartBuildResult["Skipped"] + tempBuildResult["Skipped"],
                    "Passed": tempChartBuildResult["Passed"] + tempBuildResult["Passed"],
                    "Total": tempChartBuildResult["Total"] + tempBuildResult["Total"]
                }
                chartResult[buildNumber] = result;
            } else {
                chartResult[buildNumber] = tempBuildResult;
            }
        });
    });
    return chartResult;
}

function getSelectedRows(){
    var checkedRows = $j("#tree").find(":checked");
    var selectedRows = [];

    checkedRows.each(function(){
        var parentClass = $j(this).attr("parentclass");
        var parent = $j("."+parentClass).find("input[type='checkbox']")
        if($j.inArray(parent.get(0),checkedRows)<0) {
            selectedRows.push($j(this).parent().parent());
        }
    });
    return selectedRows;
}

function getLineChartConfig(chartCategories, lineChartType, chartData){
    var yvalue = "";
    var type = "";
    if(lineChartType == 'percent') {
        yvalue = " in percentage (%)";
        type = ' (%)';
    }
    var linechart = {
        title: {
            text: 'Build Status',
            x: -20 //center
        },
        xAxis: {
            title: {
                text: 'Build number'
            },
            categories: chartCategories
        },
        yAxis: {
            title: {
                text: 'No of tests' + yvalue
            },
            plotLines: [{
                value: 0,
                width: 1,
                color: '#808080'
            }]
        },
        credits: {
            enabled: false
        },
        tooltip: {
            headerFormat: '<b>Build no: {point.x}</b><br>',
            shared: true,
            crosshairs: true
        },
        legend: {
            layout: 'vertical',
            align: 'right',
            verticalAlign: 'middle',
            borderWidth: 0
        },
        colors : ['#24A516', '#FD0505', '#AEAEAE','#67A4F5']
        ,
        plotOptions: {
            series: {
                stacking: lineChartType,
                cursor: 'pointer',
                point: {
                    events: {
                        click: function (e) {
                            var x1 = this.x;
                            var passed = this.series.chart.series[0].data[x1].y;
                            var failed = this.series.chart.series[1].data[x1].y;
                            var skipped = this.series.chart.series[2].data[x1].y;
                            var total = this.series.chart.series[3].data[x1].y;
                            var resultTitle = 'Build details for build: '+x1;
                            /*if(lineChartType == 'percent') {
                                var pieData = [['Passed',   passed],
                                        ['Failed',       failed],
                                        ['Skipped',   skipped]];
                                generatePieChart(pieData,resultTitle)
                            } else {*/
                                generatePieChart(calculatePercentage(passed,failed,skipped,total),resultTitle);
                            /*}*/

                        }
                    }
                }
            }
        },
        series: [{
            name: 'Passed' + type,
            data: chartData["Passed"]
            //color: '#24A516'
        }, {
            name: 'Failed' + type,
            data: chartData["Failed"]
            //color: '#FD0505'
        }, {
            name: 'Skipped' + type,
            data: chartData["Skipped"]
            //color: '#AEAEAE'
        }, {
            name: 'Total' + type,
            data:  chartData["Total"]
        }]
    }

    return linechart;
}

function getBarChartConfig(chartCategories, chartData){
    var barchart = {
        chart: {
            type: 'bar'
        },
        title: {
            text: 'Build Status',
            x: -20 //center
        },
        xAxis: {
            title: {
                text: 'Build number'
            },
            categories: chartCategories
        },
        yAxis: {
            title: {
                text: 'No of tests'
            },
            plotLines: [{
                value: 0,
                width: 1,
                color: '#808080'
            }]
        },
        colors : ['#24A516', '#FD0505', '#AEAEAE'],
        credits: {
            enabled: false
        },
        tooltip: {
            headerFormat: '<b>Build no: {point.x}</b><br>',
            shared: true,
            crosshairs: true
        },
        legend: {
            reversed: true
        },
        plotOptions: {
            series: {
                stacking: 'normal'
            }
        },
        series: chartData
    }

    return barchart;
}

function calculatePercentage(passed, failed, skipped, total){
    var failedPercentage = (failed * 100)/total;
    var skippedPercentage = (skipped * 100)/total;
    var passedPercentage = (passed * 100)/total;

    return [['Passed',   passedPercentage],
        ['Failed',       failedPercentage],
        ['Skipped',   skippedPercentage]];
}

function calculateIndividualPercentage(value, total){
    var percentage = (value * 100)/total;
    return percentage;
}

function getPieChartConfig(inputData, resultTitle){
    var pieChart = {
        chart: {
            plotBackgroundColor: null,
            plotBorderWidth: 1,//null,
            plotShadow: false
        },
        credits: {
            enabled: false
        },
        title: {
            text: resultTitle ? resultTitle : 'Build details for all'
        },
        tooltip: {
            pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'
        },
        plotOptions: {
            pie: {
                allowPointSelect: true,
                cursor: 'pointer',
                dataLabels: {
                    enabled: true,
                    format: '<b>{point.name}</b>: {point.percentage:.1f} %',
                    style: {
                        color: (Highcharts.theme && Highcharts.theme.contrastTextColor) || 'black'
                    }
                }
            }
        },
        colors : ['#24A516', '#FD0505', '#AEAEAE','#67A4F5'],
        series: [{
            type: 'pie',
            name: 'Build Detail',
            data: inputData
        }]
    }
    return pieChart;
}