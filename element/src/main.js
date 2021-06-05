// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue'
import App from './App'
import router from './router'
import ElementUI from "element-ui";
import "element-ui/lib/theme-chalk/index.css"

const axios=require('axios')
axios.defaults.baseURL='http://localhost:8081/api'
axios.defaults.withCredentials = true;    //携带cookie信息，保持sessionID一致性
Vue.prototype.$axios=axios

Vue.use(ElementUI)

Vue.config.productionTip = false

/* eslint-disable no-new */
new Vue({
  el: '#app',
  router,
  components: { App },
  template: '<App/>'
})
