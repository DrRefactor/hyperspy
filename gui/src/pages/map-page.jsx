import React, { Component } from 'react'
import Map from '../components/map'
import Details from '../components/details'

export default class MapPage extends Component {
  constructor(props) {
    super(props)
    this.state = {
      fetching: {
        locations: true,
        connections: true,
        stations: true,
        lines: true,
        frequencyTimetable: true
      },
      connections: null,
      locations: null,
      stations: null,
      lines: null,
      details: null,
      refreshRate: ''
    }
    this.isLoading = this.isLoading.bind(this)
    this.onConnectionClick = this.onConnectionClick.bind(this)
    this.onLocationClick = this.onLocationClick.bind(this)
    this.onStationClick = this.onStationClick.bind(this)
    this.onRefreshRateChange = this.onRefreshRateChange.bind(this)
    this.onRefreshRateSubmit = this.onRefreshRateSubmit.bind(this)
    this.refetch = this.refetch.bind(this)
    this.onLineChange = this.onLineChange.bind(this)
  }
  componentDidMount() {
    this.setState({
      fetching: {
        locations: true,
        connections: true,
        stations: true,
        lines: true,
        frequencyTimetable: true
      }
    })
    this._fetch('capsule/location')
      .then(locations => {
        this.setState((prevState) => ({ locations, fetching: { ...prevState.fetching, locations: false } }))
      })

    this._fetch('connection')
      .then(connections => {
        this.setState((prevState) => ({ connections, fetching: { ...prevState.fetching, connections: false } }))
      })

    this._fetch('station')
      .then(stations => {
        this.setState((prevState) => ({ stations, fetching: { ...prevState.fetching, stations: false } }))
      })

    this._fetch('line')
      .then(lines => {
        this.setState((prevState) => ({ lines, fetching: { ...prevState.fetching, lines: false } }))
      })

    this._fetch('timetable-time-frequency')
      .then(frequencyTimetable => {
        this.setState(prevState => ({ frequencyTimetable, fetching: { ...prevState.fetching, frequencyTimetable: false } }))
      })
  }
  _fetch(what) {
    return fetch(`http://localhost:8080/${what}`)
      .then(res => res.json())
  }
  render() {
    const { fetching, details } = this.state
    const loading = this.isLoading()

    const { connections, locations, stations } = this.state

    const className = 'content' + (this.state.details ? ' with-details' : '') + (loading ? '' : ' with-leftnav')
    return (
      <div className='map-page'>
        { loading ? null :
          <div className='leftnav'>
            <form className='inline-form' onSubmit={this.onRefreshRateSubmit.bind(this)}>
              <input type='number' placeholder="Refresh rate" min='5' className='refresh-rate'
                onChange={this.onRefreshRateChange} value={this.state.refreshRate} />
              <input className='submit' type='submit' value="Submit" />
            </form>
            <label>
              Select line to show timetable:
              <select value={(this.state.line || {}).id} onChange={this.onLineChange}>
                <option disabled selected value> -- select line -- </option>
                { this.state.lines.map(line => <option key={line.id} value={line.id}>{line.name}</option>) }
              </select>
            </label>
            <form className='inline-form' onSubmit={this.onTimetableFreqSubmit.bind(this)}>
              <input placeholder="Start hour(hh:mm)" onChange={this.onStartHourChange.bind(this)} />
              <input placeholder="Frequency(mins)" type='number' min='0' onChange={this.onFrequencyChange.bind(this)} />
              <input className='submit' type='submit' value="Submit" />
            </form>
            {
              !(this.state.line && this.state.frequencyTimetable) ? null :
              [<select value={this.state.frequencyTimetableEntry}
                onChange={this.onFrequencyTimetableEntryChange.bind(this)}>
                <option selected value=''> -- select timetable entry -- </option>
                { this.state.frequencyTimetable
                    .filter(x => x.timetableEntity.line.id == this.state.line.id)
                    .map(x => <option key={x.startHour + x.timetableEntity.id} value={x.startHour + ';' + x.timetableEntity.id}>{`${x.startHour},\nfrom:${x.timetableEntity.fromDate}`}</option>)
                }
              </select>,
              <button onClick={this.onTimetableEntryDelete.bind(this)}>Delete timetable entry</button>
              ]
            }
          </div> }
        <div className={className}>
          { loading ? <LoadingBox fetching={fetching} /> : <Map
            autoFit={false} details={details}
            connections={connections} locations={locations} stations={stations}
            onLocationClick={this.onLocationClick} onStationClick={this.onStationClick} onConnectionClick={this.onConnectionClick}
            /> }
        </div>
        { details ? <div className='details'>
            <Details node={this.state.details.node} summary={this.state.details.type} defaultOpen={true} />
          </div> : null }
      </div>
    )
  }
  onTimetableEntryDelete() {
    if (!this.state.line || !this.state.frequencyTimetable || !this.state.frequencyTimetableEntry)
      return
    const [startHour, timetable] = this.state.frequencyTimetableEntry.split(';')
    const toDelete = this.state.frequencyTimetable
      .filter(x => x.timetableEntity.line.id == this.state.line.id)
      .find(x => x.timetable == timetable && x.startHour == startHour)
    if (!toDelete)
      return alert("Error while deleting timetable entry")
    
    return fetch(`http://localhost:8080/timetable/${toDelete.timetable}?startHour=${toDelete.startHour}`, {
      method: 'DELETE',
      headers: {
        Accept: 'application/json',
        'Content-Type': 'application/json',
      }
    })
    .then(() => this.refetch())
    .then(() => {
      this.setState({ frequencyTimetableEntry: '' })
      this.onLineChange({ target: { value: this.state.line && this.state.line.id || '' } })
      alert(`Entry ${toDelete.startHour} ${toDelete.timetableEntity.fromDate} deleted`)
    })
  }
  onStartHourChange(e) {
    this.setState({ startHour: e.target.value })
  }
  onFrequencyChange(e) {
    this.setState({ frequency: e.target.value })
  }
  onFrequencyTimetableEntryChange(e) {
    this.setState({ frequencyTimetableEntry: e.target.value })
  }
  onTimetableFreqSubmit(e) {
    e.preventDefault()
    if (this.state.startHour == null || this.state.frequency == null || this.state.line == null)
      return alert('Wrong timetable params')
    const lineId = this.state.line.id
    return fetch(`http://localhost:8080/timetable`, {
      method: 'POST',
      headers: {
        Accept: 'application/json',
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        lineId,
        startHour: this.state.startHour,
        frequency: this.state.frequency,
      })
    })
    .then(() => this.refetch())
    .then(() => this.onLineChange({ target: { value: lineId } }))
  }

  onRefreshRateSubmit(e) {
    e.preventDefault()
    e.stopPropagation()
    const val = this.state.refreshRate
    if (val < 5 || Number.isNaN(val)) {
      this.setState({ refreshRate: '' })
      return alert("Minimal refresh rate is 5")
    }
    if (this.refreshInterval) {
      clearInterval(this.refreshInterval)
      this.refreshInterval = null
    }
    this.setState({ refreshRate: val })
    
    this.refreshInterval = setInterval(this.refetch, val * 1000)
  }
  onRefreshRateChange(e) {
    const val = Number.parseInt(e.target.value)
    this.setState({ refreshRate: val })
  }
  isLoading() {
    const { fetching } = this.state
    return Object.keys(fetching).some(x => fetching[x])
  }
  onLocationClick(location) {
    this.setState({ details: { type: 'location', node: location } })
  }
  onStationClick(station) {
    this.setState({ details: { type: 'station', node: station } })
  }
  onConnectionClick(connection) {
    this.setState({ details: { type: 'connection', node: connection } })
  }
  onLineChange(e) {
    const id = e.target.value
    const line = this.state.lines.find(x => x.id == id)
    let details = this.state.frequencyTimetable.filter(x => x.timetableEntity.line.id == id)
    if (!line || !details.length)
      return alert(`Incorrect line ${id}`)
    details = { type: 'timetable', node: details }
    this.setState({ line, details })
  }
  refetch() {
    return Promise.all([
      this._fetch('capsule/location'),
      this._fetch('connection'),
      this._fetch('station'),
      this._fetch('timetable-time-frequency')
    ])
      .then(results => {
        const [locations, connections, stations, frequencyTimetable] = results
        this.setState({ locations, connections, stations, frequencyTimetable })
      })
  }
}

class LoadingBox extends Component {
  constructor(props) {
    super(props)
    this.onRefChange = this.onRefChange.bind(this)
    this.animate = this.animate.bind(this)
  }
  componentWillUnmount() {
    if (this.interval) {
      clearInterval(this.interval)
      this.interval = null
    }
  }
  render() {
    const { fetching } = this.props
    const fetchInfo = this.renderFetchInfo(fetching)
    return (
      <div className='loading-box'>
        <canvas width={432} height={250} ref={this.onRefChange} />
        {fetchInfo}
      </div>
    )
  }
  renderFetchInfo(fetching = {}) {
    return Object.keys(fetching)
      .map(x => <span className='fetch-info' key={x}>{`Loading ${x}...` + (fetching[x] ? "" : " done")}</span> )
  }
  onRefChange(el) {
    if (el) {
      const ctx = el.getContext('2d')
      this.elapsedTime = 0
      this.interval = setInterval(() => this.animate(ctx, el), 1000/60)
    }
    else if (this.interval) {
      clearInterval(this.interval)
      this.interval = null
    }
  }
  
  animate(x, c) {
    const C = Math.cos
    const S = Math.sin
    const t = this.elapsedTime
    let i = 0
    let h = 0

    for(i=h=c.width=432;i--;) {
      C(t-i)>0 &&
        x.fillText('.⬤'['榁翻꺿듻ퟝ믭󫥤큰삗⢠挎ᩐ肦䰠椉䠊ᑒꊐࢀင'.charCodeAt(i/16)>>i%16&1],192+(i*h-i*i)**.5*S(t-i)/2,i/2+9)
    }
    this.elapsedTime += 1/60
  }
}