/* eslint-disable brace-style */
/* eslint-disable keyword-spacing */
/* eslint-disable no-trailing-spaces */
/* eslint-disable eqeqeq */
/* eslint-disable no-undef */
/* eslint-disable no-unused-vars */
  // eslint-disable-next-line space-before-function-paren
import _ from 'lodash';
import axios from 'axios';
let config = require('../../config');

let backendConfigurer = function () {
  switch (process.env.NODE_ENV) {
    case 'testing':
    case 'development':
      return 'http://' + config.dev.backendHost + ':' + config.dev.backendPort;
    case 'production':
      return 'https://' + config.build.backendHost + ':' + config.build.backendPort;
  }
}

let backendUrl = backendConfigurer();

let AXIOS = axios.create({
  baseURL: backendUrl
  // headers: {'Access-Control-Allow-Origin': frontendUrl}
});

export default {
  name: 'eventregistration',

  data () {
    return {
      persons: [],
      promoters: [],
      events: [],
      newPerson: '',
      personType: 'Person',
      newEvent: {
        name: '',
        date: '',
        startTime: '09:00',
        endTime: '11:00',
        company: ''
      },
      selectedPerson: '',
      selectedPromoter: '',
      selectedEvent: '',
      selectedEventP: '',
      errorPerson: '',
      errorEvent: '',
      errorRegistration: '',
      errorAssign: '',
      response: []
    }
  },
  created: function () {
    // Initializing persons
    AXIOS.get('/persons')
    .then(response => {
      this.persons = response.data;
      this.persons.forEach(person => this.getRegistrations(person.name))
    })
    .catch(e => { this.errorPerson = e });

    AXIOS.get('/events').then(response => { this.events = response.data }).catch(e => { this.errorEvent = e });
  },

  methods: {

    createPerson: function (personType, personName) {
      if(personType == 'promoter') {
        AXIOS.post('/promoters/'.concat(personName), {}, {})
        .then(response => {
          this.persons.push(response.data);
          this.promoters.push(response.data);
          this.errorPerson = '';
          this.newPerson = '';
        })
      .catch(e => {
        e = e.response.data.message ? e.response.data.message : e;
        this.errorPerson = e;
        console.log(e);
      });
      }
      else{
        AXIOS.post('/persons/'.concat(personName), {}, {})
        .then(response => {
          this.persons.push(response.data);
          this.errorPerson = '';
          this.newPerson = '';
        })
        .catch(e => {
          e = e.response.data.message ? e.response.data.message : e;
          this.errorPerson = e;
          console.log(e);
        });
      }
    },

    createCircus: function (newEvent) {
      let url = '';
      
      AXIOS.post('/circus/'.concat(newEvent.name), {}, {params: newEvent})
      .then(response => {
        this.events.push(response.data);
        this.errorEvent = '';
        this.newEvent.name = this.newEvent.make = this.newEvent.movie = this.newEvent.company = this.newEvent.artist = this.newEvent.title = '';
      })
      .catch(e => {
        e = e.response.data.message ? e.response.data.message : e;
        this.errorEvent = e;
        console.log(e);
      });
    },
    dateFormat: function (date) {
      return '1234-01-01'
    },

    createEvent: function (newEvent) {
      let url = '';

      if (newEvent.company != '' && newEvent.company != null) {
        this.createCircus(newEvent);
      } else {
        AXIOS.post('/events/'.concat(newEvent.name), {}, {params: newEvent})
      .then(response => {
        var event = response.data;
        event.company = '--';
        // var date1 = new Date();
        // date1.setFullYear('1234', '1', '1');
        // event.date = date1;
        // if(event.date.charAt(2)=='-'){
        // event.date = '1000-01-01'
        // }
        this.events.push(event);
        this.errorEvent = '';
        this.newEvent.name = this.newEvent.make = this.newEvent.movie = this.newEvent.company = this.newEvent.artist = this.newEvent.title = '';
      })
      .catch(e => {
        e = e.response.data.message ? e.response.data.message : e;
        this.errorEvent = e;
        console.log(e);
      });
      }
    },

    registerEvent: function (personName, eventName) {
      let event = this.events.find(x => x.name === eventName);
      let person = this.persons.find(x => x.name === personName);
      let params = {
        person: person.name,
        event: event.name
      };

      AXIOS.post('/register', {}, {params: params})
      .then(response => {
        person.eventsAttended.push(event)
        this.selectedPerson = '';
        this.selectedEvent = '';
        this.errorRegistration = '';
      })
      .catch(e => {
        e = e.response.data.message ? e.response.data.message : e;
        this.errorRegistration = e;
        console.log(e);
      });
    },

    getRegistrations: function (personName) {
      AXIOS.get('/events/person/'.concat(personName))
      .then(response => {
        if (!response.data || response.data.length <= 0) return;

        let indexPart = this.persons.map(x => x.name).indexOf(personName);
        this.persons[indexPart].eventsAttended = [];
        response.data.forEach(event => {
          this.persons[indexPart].eventsAttended.push(event);
        });
      })
      .catch(e => {
        e = e.response.data.message ? e.response.data.message : e;
        console.log(e);
      });
    },

    assignEvent: function (personName, eventName) {
      let promoter = this.promoters.find(x => x.name === personName);
      let event = this.events.find(x => x.name === eventName);
      let params = {
        promoter: promoter.name,
        event: event.name
      };

      AXIOS.post('/assign', {}, {params: params})
      .then(response => {
        this.selectedPerson = '';
        this.selectedEventP = '';
        this.errorAssign = '';
        console.log('assigned')
      })
      .catch(e => {
        e = e.response.data.message ? e.response.data.message : e;
        this.errorAssign = e;
        console.log(e);
      });
    }
  }
}
