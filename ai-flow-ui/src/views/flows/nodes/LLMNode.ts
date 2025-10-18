import { RectNode, RectNodeModel, h } from '@logicflow/core'

export class LLMNodeModel extends RectNodeModel {
  setAttributes() {
    this.width = 140
    this.height = 52
    this.radius = 6
  }
  
  getDefaultAnchor() {
    const { width, height, x, y, id } = this
    return [
      {
        x: x - width / 2,
        y: y,
        id: `${id}_left`,
        type: 'left'
      },
      {
        x: x + width / 2,
        y: y,
        id: `${id}_right`,
        type: 'right'
      }
    ]
  }
}

export class LLMNode extends RectNode {
  getShape() {
    const { width, height, x, y } = this.props.model
    const attrs = {
      x: x - width / 2,
      y: y - height / 2,
      width,
      height,
      rx: 6,
      ry: 6,
      stroke: '#409EFF',
      fill: '#ecf5ff',
      strokeWidth: 2
    }
    return h('rect', attrs)
  }
  
  getText() {
    const { x, y, properties } = this.props.model
    const text = properties?.name || 'LLM'
    return h('text', {
      fill: '#409EFF',
      fontSize: 14,
      x,
      y,
      textAnchor: 'middle',
      alignmentBaseline: 'middle'
    }, text)
  }
}
