import timeTrackingApp from './components/timeTrackingApp.vue';
import './../css/main.less';

Vue.use(Vuetify);


const vuetify = new Vuetify({
    dark: true,
    iconfont: 'mdi',
});

$(document).ready(() => {
    new Vue({
        render: (h) => h(timeTrackingApp),
        vuetify,
    }).$mount('#timeTrackingApp');

});