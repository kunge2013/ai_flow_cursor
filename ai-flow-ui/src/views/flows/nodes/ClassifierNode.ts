import { RectNode, RectNodeModel } from '@logicflow/core'

export class ClassifierNodeModel extends RectNodeModel {
  setAttributes() {
    this.width = 140
    this.height = 52
  }
}

export class ClassifierNodeView extends RectNode {
  // 使用默认的矩形节点视图
}
