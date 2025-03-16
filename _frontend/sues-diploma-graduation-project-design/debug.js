// 分页参数处理测试

function testPaginationHandling() {
  // 原来的代码
  function originalSizeHandling(size) {
    // 原始代码中的处理
    return Math.min(Math.max(15, size), 50);
  }

  // 修改后的代码
  function newSizeHandling(size) {
    // 直接使用用户选择的大小
    return size;
  }

  // 测试不同的分页大小
  const testSizes = [10, 15, 30, 40, 50, 60];
  
  console.log("测试分页大小处理函数：");
  console.log("------------------------------");
  console.log("页面大小\t原处理结果\t新处理结果");
  console.log("------------------------------");
  
  for (const size of testSizes) {
    const originalResult = originalSizeHandling(size);
    const newResult = newSizeHandling(size);
    console.log(`${size}\t\t${originalResult}\t\t${newResult}`);
  }
  
  console.log("------------------------------");
  console.log("结论：原始代码会将页面大小强制限制在15-50之间，即使用户选择了15，实际传递的也是15");
  console.log("修改后的代码直接使用用户选择的页面大小，确保能正确显示用户选择的记录数量");
}

// 运行测试
testPaginationHandling(); 