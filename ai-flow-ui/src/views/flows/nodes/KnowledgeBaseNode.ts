import { RectNode, RectNodeModel, h } from '@logicflow/core'

export class KnowledgeBaseNodeModel extends RectNodeModel {
  setAttributes() {
    this.width = 140
    this.height = 52
    this.radius = 6
  }
}

export class KnowledgeBaseNode extends RectNode {
  getShape() {
    const { width, height, x, y } = this.props.model
    return h('rect', {
      x: x - width / 2,
      y: y - height / 2,
      width,
      height,
      rx: 6,
      ry: 6,
      stroke: '#909399',
      fill: '#f4f4f5',
      strokeWidth: 2
    })
  }
  
  getText() {
    const { x, y, properties } = this.props.model
    const text = properties?.name || 'KnowledgeBaseNode'
    return h('text', {
      fill: '#909399',
      fontSize: 14,
      x,
      y,
      textAnchor: 'middle',
      alignmentBaseline: 'middle'
    }, text)
  }
}
