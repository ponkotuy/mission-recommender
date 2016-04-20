$(document).ready ->
  message = messages()
  recommend = renderingWhite(message)
  form = forms(recommend)

renderingWhite = (message) ->
  new Vue
    el: '#recommend'
    data:
      missions: [{name: "a", to: 1.0, around: 2.0}]
    methods:
      getMission: (params) ->
        getting = $.getJSON '/api/missions', params
        getting.success (json) =>
          @missions = json
          message.removeError()
        getting.error (error) =>
          message.setError(error.responseText)

forms = (recommend) ->
  new Vue
    el: '#control'
    data:
      pos:
        lat: 0
        lng: 0
      meter: "200"
    methods:
      change: ->
        recommend.getMission({lat: @pos.lat, lng: @pos.lng, meter: parseInt(@meter)})
    ready: ->
      navigator.geolocation.getCurrentPosition (pos) =>
        @pos.lat = pos.coords.latitude
        @pos.lng = pos.coords.longitude
        @change()

messages = ->
  new Vue
    el: '#message'
    data:
      isError: false
      message: ""
    methods:
      setError: (mes) ->
        @message = mes
        @isError = true
      removeError: ->
        @isError = false
