import { RectNode, RectNodeModel, h } from '@logicflow/core'

export class OrganizerNodeModel extends RectNodeModel {
  setAttributes() {
    this.width = 160
    this.height = 60
    this.radius = 8
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

export class OrganizerNode extends RectNode {
  getShape() {
    const { width, height, x, y } = this.props.model
    const attrs = {
      x: x - width / 2,
      y: y - height / 2,
      width,
      height,
      rx: 8,
      ry: 8,
      stroke: '#9C27B0',
      fill: '#f3e5f5',
      strokeWidth: 2
    }
    return h('rect', attrs)
  }
  
  getText() {
    const { x, y, properties } = this.props.model
    const text = properties?.name || '组织器'
    return h('text', {
      fill: '#9C27B0',
      fontSize: 14,
      fontWeight: 'bold',
      x,
      y,
      textAnchor: 'middle',
      alignmentBaseline: 'middle'
    }, text)
  }
  
  getIcon() {
    const { x, y, width, height } = this.props.model
    return h('text', {
      fill: '#9C27B0',
      fontSize: 20,
      x: x - width / 2 + 20,
      y: y,
      textAnchor: 'middle',
      alignmentBaseline: 'middle'
    }, '📋')
  }
}
