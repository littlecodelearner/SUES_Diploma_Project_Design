// API调试脚本 - 模拟分页请求
console.log("========== API请求参数分析 ==========");

// 模拟API函数
function mockApiFunction(params) {
  // 默认值与合并
  const requestParams = {
    current: 1,
    size: 15,
    isAsc: true,
    ...params
  };
  
  console.log("最终发送的API参数:", requestParams);
  return requestParams;
}

// 测试用例
const testCases = [
  {
    name: "用户选择每页15条",
    params: {
      userId: 123,
      current: 1,
      size: 15
    }
  },
  {
    name: "用户选择每页30条",
    params: {
      userId: 123,
      current: 1,
      size: 30
    }
  },
  {
    name: "带筛选条件的请求",
    params: {
      userId: 123,
      current: 2,
      size: 40,
      startDateTime: "2023-09-01T00:00:00.000Z",
      endDateTime: "2023-09-30T23:59:59.999Z",
      mealType: "DINNER"
    }
  },
  {
    name: "未指定size的请求",
    params: {
      userId: 123,
      current: 1
    }
  }
];

// 执行测试
for (const testCase of testCases) {
  console.log("\n测试用例:", testCase.name);
  console.log("原始参数:", testCase.params);
  const result = mockApiFunction(testCase.params);
  
  // 检查size参数是否被正确处理
  if (testCase.params.size) {
    console.log(`Size参数: 原始值=${testCase.params.size}, 最终值=${result.size}`);
    if (testCase.params.size !== result.size) {
      console.log("⚠️ 警告: size参数被修改了!");
    } else {
      console.log("✅ size参数保持不变");
    }
  } else {
    console.log(`Size参数: 未指定, 使用默认值=${result.size}`);
  }
}

console.log("\n========== 结论 ==========");
console.log("1. API函数会使用默认的size=15，但用户选择的size会覆盖默认值");
console.log("2. 修改后的代码现在将直接使用用户选择的分页大小，不再强制限制范围");
console.log("3. 当用户选择15/page时，API接收到的size参数为15，应该正确显示15条记录");
console.log("========== 结束 =========="); 