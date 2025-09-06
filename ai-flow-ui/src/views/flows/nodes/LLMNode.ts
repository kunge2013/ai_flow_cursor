import { RectNode, RectNodeModel, h } from '@logicflow/core'

export class LLMNodeModel extends RectNodeModel {
  setAttributes() {
    this.width = 140
    this.height = 52
  }
}

export class LLMNodeView extends RectNode {
  getShape() {
    const { width, height, x, y, id } = this.props.model
    const rect = h('rect', {
      x: x - width / 2,
      y: y - height / 2,
      width,
      height,
      rx: 6,
      ry: 6,
      stroke: '#409eff',
      fill: '#fff',
      strokeWidth: 1
    })
    // plus button at right center
    const r = 10
    const cx = x + width / 2 + r + 2
    const cy = y
    const plusCircle = h('circle', {
      cx,
      cy,
      r,
      fill: '#409eff',
      stroke: 'transparent'
    })
    const plusV = h('line', { 
      x1: cx, 
      y1: cy - 5, 
      x2: cx, 
      y2: cy + 5, 
      stroke: '#fff', 
      strokeWidth: 2, 
      strokeLinecap: 'round' 
    })
    const plusH = h('line', { 
      x1: cx - 5, 
      y1: cy, 
      x2: cx + 5, 
      y2: cy, 
      stroke: '#fff', 
      strokeWidth: 2, 
      strokeLinecap: 'round' 
    })
    const plusGroup = h('g', {
      className: 'llm-plus-btn',
      style: 'cursor: pointer;',
      onClick: () => {
        // emit a custom event for Vue layer to open the add menu for this node
        ;(this as any).props?.model?.graphModel?.eventCenter?.emit('llm:plus-click', { id })
      }
    }, [plusCircle, plusV, plusH])
    return h('g', {}, [rect, plusGroup])
  }
}
