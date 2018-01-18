import React, { Component } from 'react'

export default class Details extends Component {
  constructor(props) {
    super(props)
  }
  render() {
    const { node, summary = "", defaultOpen } = this.props
    let details = <DetailsTree node={node} summary={(summary ? summary + " " : "") +  "details"} open={defaultOpen} />
    if (Array.isArray(node))
      details = node.map((x, i) => <DetailsTree node={x} key={i} summary="" open={defaultOpen} />)
    return (
      <div className='details'>
        {details}
      </div>
    )
  }
}

class DetailsTree extends Component {
  constructor(props) {
    super(props)
  }
  render() {
    const { node, summary = "", open } = this.props
    const { arrays, objects, strings } = this.groupByTypes(node)
    const arrayElems = this.renderArrays(arrays, open)
    const objectElems = this.renderObjects(objects, open)
    const stringElems = this.renderStrings(strings)
  
    return (
      <details className='details-tree' open={open || !summary}>
        <summary>
          {summary}
        </summary>
        <div className='details-content'>
          {stringElems}
          {objectElems}
          {arrayElems}
        </div>
      </details>
    )
  }
  renderStrings(strings = []) {
    return strings.map(s => <span key={s.summary} className='details-tree-label'>{`${s.summary}: ${s.node}\n`}</span>)
  }
  renderObjects(objects = [], open) {
    return objects.map(o => <DetailsTree key={o.summary} summary={o.summary} node={o.node} open={open} />)
  }
  renderArrays(arrays = [], open) {
    return arrays.map(a => {
      return (
        <div className='details-tree-list'>
          <span className='title'>{a.summary}</span>
          <ul>
            {a.nodes.map(n => <li>{<DetailsTree node={n} open={open} />}</li>)}
          </ul>
        </div>
      )
    })
  }

  groupByTypes(node = {}) {
    return Object.keys(node).reduce((r, x) => {
      if (node[x] instanceof Array)
        r.arrays.push({ summary: x, nodes: node[x] })
      else if (node[x] instanceof Object)
        r.objects.push({ summary: x, node: node[x] })
      else
        r.strings.push({ summary: x, node: node[x] })
      return r;
    }, { arrays: [], objects: [], strings: [] })
  }
}