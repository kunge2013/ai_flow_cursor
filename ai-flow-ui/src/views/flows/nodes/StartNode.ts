import { RectNode, RectNodeModel, h } from '@logicflow/core'

export class StartNodeModel extends RectNodeModel {
  setAttributes() {
    this.width = 120
    this.height = 40
    this.radius = 20
  }
  
  getDefaultAnchor() {
    const { width, height, x, y, id } = this
    return [
      {
        x: x + width / 2,
        y: y,
        id: `${id}_right`,
        type: 'right'
      }
    ]
  }
}

export class StartNode extends RectNode {
  getShape() {
    const { width, height, x, y } = this.props.model
    const attrs = {
      x: x - width / 2,
      y: y - height / 2,
      width,
      height,
      rx: 20,
      ry: 20,
      stroke: '#67C23A',
      fill: '#f0f9ff',
      strokeWidth: 2
    }
    return h('rect', attrs)
  }
  
  getText() {
    const { x, y, properties } = this.props.model
    const text = properties?.name || '开始'
    return h('text', {
      fill: '#67C23A',
      fontSize: 14,
      x,
      y,
      textAnchor: 'middle',
      alignmentBaseline: 'middle'
    }, text)
  }
}
