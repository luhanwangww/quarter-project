package com.ucd.ecs235.compiler;

/* Generated By:JJTree: Do not edit this line. ASTActionName.java Version 7.0 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=false,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
public
class ASTActionName extends SimpleNode {
  public ASTActionName(int id) {
    super(id);
  }

  public ASTActionName(Ponder p, int id) {
    super(p, id);
  }
  private String name;

  public void setName(String n) {
    name = n;
  }

  public String toString() {
    return "ActionName:" + name;
  }
}
/* JavaCC - OriginalChecksum=aec32a41a940f300c08e5ec9fac3b9e0 (do not edit this line) */
