<template>
  <div>
    <el-row>
      <el-col :span="6">
        <div class="card">
          <el-row>
            <el-col :span="12">
              <el-progress type="circle" :percentage="25"></el-progress>
            </el-col>
            <el-col :span="12">
              <h1>实验数据1</h1>
              <p>这个是实验数据1实验数据1实验数据1实验数据1实验数据1实验数据1</p>
            </el-col>
          </el-row>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="card">
          <el-row>
            <el-col :span="12">
              <el-progress type="circle" :percentage="50" status="exception"></el-progress>
            </el-col>
            <el-col :span="12">
              <h1>实验数据2</h1>
              <p>这个是实验数据2实验数据2实验数据2实验数据2实验数据2实验数据2</p>
            </el-col>
          </el-row>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="card">
          <el-progress type="circle" :percentage="100" status="success"></el-progress>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="card">
          <el-progress type="circle" :percentage="50" status="warning"></el-progress>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script>
export default {
  name: "DashBoard"
}
</script>

<style scoped>
.card{
  background-color: white;
  border-radius: 15px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  text-align: left;
  font-size: 13px;
  margin: 0 10px 10px 0;
  padding: 10px 10px 10px 10px;
}
</style>
