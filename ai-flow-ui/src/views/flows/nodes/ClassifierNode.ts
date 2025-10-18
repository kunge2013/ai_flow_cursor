import { RectNode, RectNodeModel, h } from '@logicflow/core'

export class ClassifierNodeModel extends RectNodeModel {
  setAttributes() {
    this.width = 140
    this.height = 52
    this.radius = 6
  }
}

export class ClassifierNode extends RectNode {
  getShape() {
    const { width, height, x, y } = this.props.model
    return h('rect', {
      x: x - width / 2,
      y: y - height / 2,
      width,
      height,
      rx: 6,
      ry: 6,
      stroke: '#E6A23C',
      fill: '#fdf6ec',
      strokeWidth: 2
    })
  }
  
  getText() {
    const { x, y, properties } = this.props.model
    const text = properties?.name || '分类器'
    return h('text', {
      fill: '#E6A23C',
      fontSize: 14,
      x,
      y,
      textAnchor: 'middle',
      alignmentBaseline: 'middle'
    }, text)
  }
}
