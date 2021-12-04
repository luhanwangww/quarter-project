package com.ucd.ecs235.compiler;/* Ponder.java */
/* Generated By:JJTree&JavaCC: Do not edit this line. Ponder.java */
/**
 * An Arithmetic Grammar.
 */

import com.ucd.ecs235.dto.ActionFilter;
import com.ucd.ecs235.dto.AuthPolicy;
import com.ucd.ecs235.dto.RefrainPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Ponder/*@bgen(jjtree)*/implements PonderTreeConstants, PonderConstants {/*@bgen(jjtree)*/
  protected static JJTPonderState jjtree = new JJTPonderState();
    static Map<String, List<String>> functionToScopeMap = new HashMap<>();

    /** Main entry point. */
  public static void main(String args[]) throws FileNotFoundException, UnsupportedEncodingException {
    System.out.println("Reading from config file...");
          Reader reader = new InputStreamReader(new FileInputStream("policies.txt"), "UTF-8");
    //    Ponder t = new Ponder(System.in);
          Ponder t = new Ponder(reader);
    try {
      SimpleNode n = t.Start();
      n.dump("");
//      policyTestPrint();
        processAccessControlMap();
        printFunctionToScopeMap();
    } catch (Exception e) {
      System.out.println("Oops.");
      System.out.println(e.getMessage());
      //e.printStackTrace();
    }
  }

  private static void policyTestPrint(){
      ArrayList<ActionFilter> actionFilters = SimpleNode.afList;
      ArrayList<AuthPolicy> authPolicies = SimpleNode.apList;
      ArrayList<RefrainPolicy> refrainPolicies = SimpleNode.rpList;
      AuthPolicy policy1 = authPolicies.get(0);
        System.out.println(policy1.isAuthPlus());
        System.out.println(policy1.getPolicyName());
        System.out.println(policy1.getSubjectType());
      System.out.println(policy1.getSubjectDomain());
        System.out.println(policy1.getTargetType());
        System.out.println(policy1.getTargetDomain());
        List<String> actionList = policy1.getAction();
        for(String action:actionList){
            System.out.println(action);
        }
        ActionFilter policy2 = actionFilters.get(0);
        System.out.println(policy2.getFilterName());
        System.out.println(policy2.getSubjectType());
        System.out.println(policy2.getSubjectDomain());
        System.out.println(policy2.getTargetType());
        System.out.println(policy2.getTargetDomain());
        System.out.println(policy2.getActionName());
        System.out.println(policy2.getCondition());
        HashMap<String, String> defaultIn = (HashMap<String, String>) policy2.getDefaultIn();
        for(String name : defaultIn.keySet()){
            System.out.println(name+"="+defaultIn.get(name));
        }
        HashMap<String, String> in = (HashMap<String, String>) policy2.getIn();
        for(String name : in.keySet()){
            System.out.println(name+"="+in.get(name));
        }
        HashMap<String, String> out = (HashMap<String, String>) policy2.getOut();
        if(out!=null) {
            for (String name : out.keySet()) {
                System.out.println(name + "=" + out.get(name));
            }
        }
        System.out.println("result="+policy2.getResult());
        RefrainPolicy policy3 = refrainPolicies.get(0);
        System.out.println(policy3.getPolicyName());
        System.out.println(policy3.getSubjectType());
      System.out.println(policy3.getSubjectDomain());
        System.out.println(policy3.getTargetType());
        System.out.println(policy3.getTargetDomain());
        System.out.println(policy3.getAction());
  }

    public static void processAccessControlMap(){
      ArrayList<ActionFilter> actionFilters = SimpleNode.afList;
      ArrayList<AuthPolicy> authPolicies = SimpleNode.apList;
      ArrayList<RefrainPolicy> refrainPolicies = SimpleNode.rpList;
        authPolicies.forEach(authPolicy -> {
            List<String> actionList = authPolicy.getAction();
            String subjectType = authPolicy.getSubjectType();
            subjectType = subjectType.replace("<", "").replace(">", "");
            String subjectDomain = authPolicy.getSubjectDomain();
            String[] subjectDomainSeq = subjectDomain.split("/");
            subjectDomain = subjectDomainSeq[subjectDomainSeq.length-1];
            String targetType = authPolicy.getTargetType();
            targetType = targetType.replace("<", "").replace(">", "");
            String targetDomain = authPolicy.getTargetDomain();
            String[] targetDomainSeq = targetDomain.split("/");
            targetDomain = targetDomainSeq[targetDomainSeq.length-1];

            if(targetType.equals("Class")){
                for(String action:actionList){
                    action = action.replace("(", "").replace(")", "");
                    String key = targetDomain+"#_#"+action;
                    if(subjectType.equals("Authentication")) {
                        if(authPolicy.isAuthPlus()) {
                            if (functionToScopeMap.containsKey(key)) {
                                functionToScopeMap.get(key).add(subjectDomain);
                            } else {
                                ArrayList<String> subjects = new ArrayList<>();
                                subjects.add(subjectDomain);
                                functionToScopeMap.put(key, subjects);
                            }
                        }else {
                            if(functionToScopeMap.containsKey(key)){
                                if(functionToScopeMap.get(key).contains(subjectDomain)){
                                    functionToScopeMap.get(key).remove(subjectDomain);
                                }
                            }
                        }
                    }
                }
            }
        });

        refrainPolicies.forEach(refrainPolicy -> {
            String action= refrainPolicy.getAction();
            action = action.replace("(", "").replace(")", "");
            String subjectType = refrainPolicy.getSubjectType();
            subjectType = subjectType.replace("<", "").replace(">", "");
            String subjectDomain = refrainPolicy.getSubjectDomain();
            String[] subjectDomainSeq = subjectDomain.split("/");
            subjectDomain = subjectDomainSeq[subjectDomainSeq.length-1];
            String targetType = refrainPolicy.getTargetType();
            targetType = targetType.replace("<", "").replace(">", "");
            String targetDomain = refrainPolicy.getTargetDomain();
            String[] targetDomainSeq = targetDomain.split("/");
            targetDomain = targetDomainSeq[targetDomainSeq.length-1];

            if(targetType.equals("Class")){
                String key = targetDomain+"#_#"+action;
                if(subjectType.equals("Authentication")) {
                    if(functionToScopeMap.containsKey(key)){
                        if(functionToScopeMap.get(key).contains(subjectDomain)){
                            functionToScopeMap.get(key).remove(subjectDomain);
                        }
                    }
                }
            }
        });
    }

    public static void printFunctionToScopeMap(){
      System.out.println();
      System.out.println("Map:");
        for(String key:functionToScopeMap.keySet()){
            System.out.print("{"+key+": ");
            List<String> list = functionToScopeMap.get(key);
            System.out.print("[");
            for (String scope: list) {
                if(list.indexOf(scope)==list.size()-1){
                    System.out.print(scope);
                }else {
                    System.out.print(scope + ",");
                }
            }
            System.out.println("]}");
        }
    }

  public static void compile(String fileName) throws IOException {
        System.out.println("Reading from config file...");
        Resource resource = new ClassPathResource(fileName);
        InputStream input = resource.getInputStream();

//            Ponder t = new Ponder(System.in);
        Ponder t = new Ponder(input);
        try {
            SimpleNode n = t.Start();
            n.dump("");
        } catch (Exception e) {
            System.out.println("Oops.");
            System.out.println(e.getMessage());
            //e.printStackTrace();
        }
    }

    public static List<AuthPolicy> getAuthPolicies(){
      return SimpleNode.apList;
    }
    public static List<ActionFilter> getActionFilters(){
        return SimpleNode.afList;
    }
    public static List<RefrainPolicy> getRefrainPolicies(){
        return SimpleNode.rpList;
    }

/** Main production. */
  static final public SimpleNode Start() throws ParseException {/*@bgen(jjtree) Start */
  ASTStart jjtn000 = new ASTStart(JJTSTART);
  boolean jjtc000 = true;
  jjtree.openNodeScope(jjtn000);
    try {
      label_1:
      while (true) {
        switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
        case INSTANCE:{
          ;
          break;
          }
        default:
          jj_la1[0] = jj_gen;
          break label_1;
        }
        PolicyStatement();
      }
      jj_consume_token(0);
jjtree.closeNodeScope(jjtn000, true);
    jjtc000 = false;
{if ("" != null) return jjtn000;}
    } catch (Throwable jjte000) {
if (jjtc000) {
      jjtree.clearNodeScope(jjtn000);
      jjtc000 = false;
    } else {
      jjtree.popNode();
    }
    if (jjte000 instanceof RuntimeException) {
      {if (true) throw (RuntimeException)jjte000;}
    }
    if (jjte000 instanceof ParseException) {
      {if (true) throw (ParseException)jjte000;}
    }
    {if (true) throw (Error)jjte000;}
    } finally {
if (jjtc000) {
      jjtree.closeNodeScope(jjtn000, true);
    }
    }
    throw new Error("Missing return statement in function");
}

/** auth policy */
  static final public 
void PolicyName() throws ParseException {/*@bgen(jjtree) PolicyName */
    ASTPolicyName jjtn000 = new ASTPolicyName(JJTPOLICYNAME);
    boolean jjtc000 = true;
    jjtree.openNodeScope(jjtn000);Token t;
    try {
      t = jj_consume_token(STRING);
jjtree.closeNodeScope(jjtn000, true);
      jjtc000 = false;
jjtn000.setName(t.image);
    } finally {
if (jjtc000) {
        jjtree.closeNodeScope(jjtn000, true);
      }
    }
}

  static final public void DomainScopeExpression() throws ParseException {/*@bgen(jjtree) DomainScopeExpression */
    ASTDomainScopeExpression jjtn000 = new ASTDomainScopeExpression(JJTDOMAINSCOPEEXPRESSION);
    boolean jjtc000 = true;
    jjtree.openNodeScope(jjtn000);Token t;
    try {
      t = jj_consume_token(DOMAIN);
jjtree.closeNodeScope(jjtn000, true);
     jjtc000 = false;
jjtn000.setName(t.image);
    } finally {
if (jjtc000) {
       jjtree.closeNodeScope(jjtn000, true);
     }
    }
}

  static final public void ActionItem() throws ParseException {/*@bgen(jjtree) ActionItem */
    ASTActionItem jjtn000 = new ASTActionItem(JJTACTIONITEM);
    boolean jjtc000 = true;
    jjtree.openNodeScope(jjtn000);Token t;
    try {
      t = jj_consume_token(ACTIONITEM);
jjtree.closeNodeScope(jjtn000, true);
    jjtc000 = false;
jjtn000.setName(t.image);
    } finally {
if (jjtc000) {
      jjtree.closeNodeScope(jjtn000, true);
    }
    }
}

  static final public void ActionList() throws ParseException {/*@bgen(jjtree) ActionList */
  ASTActionList jjtn000 = new ASTActionList(JJTACTIONLIST);
  boolean jjtc000 = true;
  jjtree.openNodeScope(jjtn000);
    try {
      ActionItem();
      label_2:
      while (true) {
        switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
        case COMMA:{
          ;
          break;
          }
        default:
          jj_la1[1] = jj_gen;
          break label_2;
        }
        jj_consume_token(COMMA);
        ActionItem();
      }
    } catch (Throwable jjte000) {
if (jjtc000) {
        jjtree.clearNodeScope(jjtn000);
        jjtc000 = false;
      } else {
        jjtree.popNode();
      }
      if (jjte000 instanceof RuntimeException) {
        {if (true) throw (RuntimeException)jjte000;}
      }
      if (jjte000 instanceof ParseException) {
        {if (true) throw (ParseException)jjte000;}
      }
      {if (true) throw (Error)jjte000;}
    } finally {
if (jjtc000) {
        jjtree.closeNodeScope(jjtn000, true);
      }
    }
}

  static final public void ConstraintExpression() throws ParseException {/*@bgen(jjtree) ConstraintExpression */
  ASTConstraintExpression jjtn000 = new ASTConstraintExpression(JJTCONSTRAINTEXPRESSION);
  boolean jjtc000 = true;
  jjtree.openNodeScope(jjtn000);
    try {
      jj_consume_token(STRING);
    } finally {
if (jjtc000) {
        jjtree.closeNodeScope(jjtn000, true);
      }
    }
}

  static final public void Instance() throws ParseException {/*@bgen(jjtree) Instance */
  ASTInstance jjtn000 = new ASTInstance(JJTINSTANCE);
  boolean jjtc000 = true;
  jjtree.openNodeScope(jjtn000);
    try {
      jj_consume_token(INSTANCE);
    } finally {
if (jjtc000) {
        jjtree.closeNodeScope(jjtn000, true);
      }
    }
}

  static final public void AuthPlus() throws ParseException {/*@bgen(jjtree) AuthPlus */
  ASTAuthPlus jjtn000 = new ASTAuthPlus(JJTAUTHPLUS);
  boolean jjtc000 = true;
  jjtree.openNodeScope(jjtn000);
    try {
      jj_consume_token(AUTHPlUS);
    } finally {
if (jjtc000) {
        jjtree.closeNodeScope(jjtn000, true);
      }
    }
}

  static final public void AuthMinus() throws ParseException {/*@bgen(jjtree) AuthMinus */
  ASTAuthMinus jjtn000 = new ASTAuthMinus(JJTAUTHMINUS);
  boolean jjtc000 = true;
  jjtree.openNodeScope(jjtn000);
    try {
      jj_consume_token(AUTHMINUS);
    } finally {
if (jjtc000) {
        jjtree.closeNodeScope(jjtn000, true);
      }
    }
}

  static final public void Type() throws ParseException {/*@bgen(jjtree) Type */
    ASTType jjtn000 = new ASTType(JJTTYPE);
    boolean jjtc000 = true;
    jjtree.openNodeScope(jjtn000);Token t;
    try {
      t = jj_consume_token(TYPE);
jjtree.closeNodeScope(jjtn000, true);
     jjtc000 = false;
jjtn000.setName(t.image);
    } finally {
if (jjtc000) {
       jjtree.closeNodeScope(jjtn000, true);
     }
    }
}

  static final public void Subject() throws ParseException {/*@bgen(jjtree) Subject */
  ASTSubject jjtn000 = new ASTSubject(JJTSUBJECT);
  boolean jjtc000 = true;
  jjtree.openNodeScope(jjtn000);
    try {
      jj_consume_token(SUBJECT);
      switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
      case TYPE:{
        Type();
        break;
        }
      default:
        jj_la1[2] = jj_gen;
        ;
      }
      DomainScopeExpression();
      jj_consume_token(SEMICOLON);
    } catch (Throwable jjte000) {
if (jjtc000) {
        jjtree.clearNodeScope(jjtn000);
        jjtc000 = false;
      } else {
        jjtree.popNode();
      }
      if (jjte000 instanceof RuntimeException) {
        {if (true) throw (RuntimeException)jjte000;}
      }
      if (jjte000 instanceof ParseException) {
        {if (true) throw (ParseException)jjte000;}
      }
      {if (true) throw (Error)jjte000;}
    } finally {
if (jjtc000) {
        jjtree.closeNodeScope(jjtn000, true);
      }
    }
}

  static final public void Target() throws ParseException {/*@bgen(jjtree) Target */
  ASTTarget jjtn000 = new ASTTarget(JJTTARGET);
  boolean jjtc000 = true;
  jjtree.openNodeScope(jjtn000);
    try {
      jj_consume_token(TARGET);
      switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
      case TYPE:{
        Type();
        break;
        }
      default:
        jj_la1[3] = jj_gen;
        ;
      }
      DomainScopeExpression();
      jj_consume_token(SEMICOLON);
    } catch (Throwable jjte000) {
if (jjtc000) {
        jjtree.clearNodeScope(jjtn000);
        jjtc000 = false;
      } else {
        jjtree.popNode();
      }
      if (jjte000 instanceof RuntimeException) {
        {if (true) throw (RuntimeException)jjte000;}
      }
      if (jjte000 instanceof ParseException) {
        {if (true) throw (ParseException)jjte000;}
      }
      {if (true) throw (Error)jjte000;}
    } finally {
if (jjtc000) {
        jjtree.closeNodeScope(jjtn000, true);
      }
    }
}

  static final public void Action() throws ParseException {/*@bgen(jjtree) Action */
  ASTAction jjtn000 = new ASTAction(JJTACTION);
  boolean jjtc000 = true;
  jjtree.openNodeScope(jjtn000);
    try {
      jj_consume_token(ACTION);
      ActionList();
      jj_consume_token(SEMICOLON);
    } catch (Throwable jjte000) {
if (jjtc000) {
        jjtree.clearNodeScope(jjtn000);
        jjtc000 = false;
      } else {
        jjtree.popNode();
      }
      if (jjte000 instanceof RuntimeException) {
        {if (true) throw (RuntimeException)jjte000;}
      }
      if (jjte000 instanceof ParseException) {
        {if (true) throw (ParseException)jjte000;}
      }
      {if (true) throw (Error)jjte000;}
    } finally {
if (jjtc000) {
        jjtree.closeNodeScope(jjtn000, true);
      }
    }
}

  static final public void When() throws ParseException {/*@bgen(jjtree) When */
  ASTWhen jjtn000 = new ASTWhen(JJTWHEN);
  boolean jjtc000 = true;
  jjtree.openNodeScope(jjtn000);
    try {
      jj_consume_token(WHEN);
      ConstraintExpression();
      jj_consume_token(SEMICOLON);
    } catch (Throwable jjte000) {
if (jjtc000) {
        jjtree.clearNodeScope(jjtn000);
        jjtc000 = false;
      } else {
        jjtree.popNode();
      }
      if (jjte000 instanceof RuntimeException) {
        {if (true) throw (RuntimeException)jjte000;}
      }
      if (jjte000 instanceof ParseException) {
        {if (true) throw (ParseException)jjte000;}
      }
      {if (true) throw (Error)jjte000;}
    } finally {
if (jjtc000) {
        jjtree.closeNodeScope(jjtn000, true);
      }
    }
}

  static final public void AuthPlusPolicyContent() throws ParseException {/*@bgen(jjtree) AuthPlusPolicyContent */
  ASTAuthPlusPolicyContent jjtn000 = new ASTAuthPlusPolicyContent(JJTAUTHPLUSPOLICYCONTENT);
  boolean jjtc000 = true;
  jjtree.openNodeScope(jjtn000);
    try {
      AuthPlus();
      PolicyName();
      jj_consume_token(LCURLYBRACE);
      Subject();
      Target();
      jj_consume_token(ACTION);
      switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
      case ACTIONITEM:{
        ActionList();
        jj_consume_token(SEMICOLON);
        break;
        }
      case STRING:{
        ActionFilter();
        break;
        }
      default:
        jj_la1[4] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
      jj_consume_token(RCURLYBRACE);
    } catch (Throwable jjte000) {
if (jjtc000) {
        jjtree.clearNodeScope(jjtn000);
        jjtc000 = false;
      } else {
        jjtree.popNode();
      }
      if (jjte000 instanceof RuntimeException) {
        {if (true) throw (RuntimeException)jjte000;}
      }
      if (jjte000 instanceof ParseException) {
        {if (true) throw (ParseException)jjte000;}
      }
      {if (true) throw (Error)jjte000;}
    } finally {
if (jjtc000) {
        jjtree.closeNodeScope(jjtn000, true);
      }
    }
}

  static final public void AuthMinusPolicyContent() throws ParseException {/*@bgen(jjtree) AuthMinusPolicyContent */
  ASTAuthMinusPolicyContent jjtn000 = new ASTAuthMinusPolicyContent(JJTAUTHMINUSPOLICYCONTENT);
  boolean jjtc000 = true;
  jjtree.openNodeScope(jjtn000);
    try {
      AuthMinus();
      PolicyName();
      jj_consume_token(LCURLYBRACE);
      Subject();
      Target();
      Action();
      switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
      case WHEN:{
        When();
        break;
        }
      default:
        jj_la1[5] = jj_gen;
        ;
      }
      jj_consume_token(RCURLYBRACE);
    } catch (Throwable jjte000) {
if (jjtc000) {
        jjtree.clearNodeScope(jjtn000);
        jjtc000 = false;
      } else {
        jjtree.popNode();
      }
      if (jjte000 instanceof RuntimeException) {
        {if (true) throw (RuntimeException)jjte000;}
      }
      if (jjte000 instanceof ParseException) {
        {if (true) throw (ParseException)jjte000;}
      }
      {if (true) throw (Error)jjte000;}
    } finally {
if (jjtc000) {
        jjtree.closeNodeScope(jjtn000, true);
      }
    }
}

  static final public void AuthPolicy() throws ParseException {/*@bgen(jjtree) AuthPolicy */
  ASTAuthPolicy jjtn000 = new ASTAuthPolicy(JJTAUTHPOLICY);
  boolean jjtc000 = true;
  jjtree.openNodeScope(jjtn000);
    try {
      switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
      case AUTHPlUS:{
        AuthPlusPolicyContent();
        break;
        }
      case AUTHMINUS:{
        AuthMinusPolicyContent();
        break;
        }
      default:
        jj_la1[6] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    } catch (Throwable jjte000) {
if (jjtc000) {
        jjtree.clearNodeScope(jjtn000);
        jjtc000 = false;
      } else {
        jjtree.popNode();
      }
      if (jjte000 instanceof RuntimeException) {
        {if (true) throw (RuntimeException)jjte000;}
      }
      if (jjte000 instanceof ParseException) {
        {if (true) throw (ParseException)jjte000;}
      }
      {if (true) throw (Error)jjte000;}
    } finally {
if (jjtc000) {
        jjtree.closeNodeScope(jjtn000, true);
      }
    }
}

  static final public void ParameterName() throws ParseException {/*@bgen(jjtree) ParameterName */
    ASTParameterName jjtn000 = new ASTParameterName(JJTPARAMETERNAME);
    boolean jjtc000 = true;
    jjtree.openNodeScope(jjtn000);Token t;
    try {
      t = jj_consume_token(STRING);
jjtree.closeNodeScope(jjtn000, true);
      jjtc000 = false;
jjtn000.setName(t.image);
    } finally {
if (jjtc000) {
        jjtree.closeNodeScope(jjtn000, true);
      }
    }
}

  static final public void ParameterValue() throws ParseException {/*@bgen(jjtree) ParameterValue */
    ASTParameterValue jjtn000 = new ASTParameterValue(JJTPARAMETERVALUE);
    boolean jjtc000 = true;
    jjtree.openNodeScope(jjtn000);Token t;
    try {
      t = jj_consume_token(PARAMETER);
jjtree.closeNodeScope(jjtn000, true);
      jjtc000 = false;
jjtn000.setName(t.image);
    } finally {
if (jjtc000) {
        jjtree.closeNodeScope(jjtn000, true);
      }
    }
}

  static final public void ActionIn() throws ParseException {/*@bgen(jjtree) ActionIn */
  ASTActionIn jjtn000 = new ASTActionIn(JJTACTIONIN);
  boolean jjtc000 = true;
  jjtree.openNodeScope(jjtn000);
    try {
      label_3:
      while (true) {
        jj_consume_token(IN);
        ParameterName();
        jj_consume_token(30);
        ParameterValue();
        jj_consume_token(SEMICOLON);
        switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
        case IN:{
          ;
          break;
          }
        default:
          jj_la1[7] = jj_gen;
          break label_3;
        }
      }
    } catch (Throwable jjte000) {
if (jjtc000) {
        jjtree.clearNodeScope(jjtn000);
        jjtc000 = false;
      } else {
        jjtree.popNode();
      }
      if (jjte000 instanceof RuntimeException) {
        {if (true) throw (RuntimeException)jjte000;}
      }
      if (jjte000 instanceof ParseException) {
        {if (true) throw (ParseException)jjte000;}
      }
      {if (true) throw (Error)jjte000;}
    } finally {
if (jjtc000) {
        jjtree.closeNodeScope(jjtn000, true);
      }
    }
}

  static final public void ActionOut() throws ParseException {/*@bgen(jjtree) ActionOut */
  ASTActionOut jjtn000 = new ASTActionOut(JJTACTIONOUT);
  boolean jjtc000 = true;
  jjtree.openNodeScope(jjtn000);
    try {
      label_4:
      while (true) {
        jj_consume_token(OUT);
        ParameterName();
        jj_consume_token(30);
        ParameterValue();
        jj_consume_token(SEMICOLON);
        switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
        case OUT:{
          ;
          break;
          }
        default:
          jj_la1[8] = jj_gen;
          break label_4;
        }
      }
    } catch (Throwable jjte000) {
if (jjtc000) {
       jjtree.clearNodeScope(jjtn000);
       jjtc000 = false;
     } else {
       jjtree.popNode();
     }
     if (jjte000 instanceof RuntimeException) {
       {if (true) throw (RuntimeException)jjte000;}
     }
     if (jjte000 instanceof ParseException) {
       {if (true) throw (ParseException)jjte000;}
     }
     {if (true) throw (Error)jjte000;}
    } finally {
if (jjtc000) {
       jjtree.closeNodeScope(jjtn000, true);
     }
    }
}

  static final public void ActionResult() throws ParseException {/*@bgen(jjtree) ActionResult */
  ASTActionResult jjtn000 = new ASTActionResult(JJTACTIONRESULT);
  boolean jjtc000 = true;
  jjtree.openNodeScope(jjtn000);
    try {
      jj_consume_token(RESULT);
      jj_consume_token(30);
      ParameterValue();
      jj_consume_token(SEMICOLON);
    } catch (Throwable jjte000) {
if (jjtc000) {
        jjtree.clearNodeScope(jjtn000);
        jjtc000 = false;
      } else {
        jjtree.popNode();
      }
      if (jjte000 instanceof RuntimeException) {
        {if (true) throw (RuntimeException)jjte000;}
      }
      if (jjte000 instanceof ParseException) {
        {if (true) throw (ParseException)jjte000;}
      }
      {if (true) throw (Error)jjte000;}
    } finally {
if (jjtc000) {
        jjtree.closeNodeScope(jjtn000, true);
      }
    }
}

    static final public void ActionName() throws ParseException {/*@bgen(jjtree) ActionName */
        ASTActionName jjtn000 = new ASTActionName(JJTACTIONNAME);
        boolean jjtc000 = true;
        jjtree.openNodeScope(jjtn000);Token t;
        try {
            t = jj_consume_token(STRING);
            jj_consume_token(31);
            jj_consume_token(STRING);
            label_5:
            while (true) {
                switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
                    case COMMA:{
                        ;
                        break;
                    }
                    default:
                        jj_la1[9] = jj_gen;
                        break label_5;
                }
                jj_consume_token(COMMA);
                jj_consume_token(STRING);
            }
            jj_consume_token(32);
            jjtree.closeNodeScope(jjtn000, true);
            jjtc000 = false;
            jjtn000.setName(t.image);
        } finally {
            if (jjtc000) {
                jjtree.closeNodeScope(jjtn000, true);
            }
        }
    }

  static final public void ActionCondition() throws ParseException {/*@bgen(jjtree) ActionCondition */
  ASTActionCondition jjtn000 = new ASTActionCondition(JJTACTIONCONDITION);
  boolean jjtc000 = true;
  jjtree.openNodeScope(jjtn000);
    try {
      jj_consume_token(IF);
      jj_consume_token(31);
      ActionItem();
      jj_consume_token(32);
    } catch (Throwable jjte000) {
if (jjtc000) {
        jjtree.clearNodeScope(jjtn000);
        jjtc000 = false;
      } else {
        jjtree.popNode();
      }
      if (jjte000 instanceof RuntimeException) {
        {if (true) throw (RuntimeException)jjte000;}
      }
      if (jjte000 instanceof ParseException) {
        {if (true) throw (ParseException)jjte000;}
      }
      {if (true) throw (Error)jjte000;}
    } finally {
if (jjtc000) {
        jjtree.closeNodeScope(jjtn000, true);
      }
    }
}

  static final public void ActionFilter() throws ParseException {/*@bgen(jjtree) ActionFilter */
  ASTActionFilter jjtn000 = new ASTActionFilter(JJTACTIONFILTER);
  boolean jjtc000 = true;
  jjtree.openNodeScope(jjtn000);
    try {
      ActionName();
      jj_consume_token(LCURLYBRACE);
      ActionIn();
      jj_consume_token(RCURLYBRACE);
      switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
      case IF:{
        ActionCondition();
        jj_consume_token(LCURLYBRACE);
        ActionIn();
        switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
        case OUT:{
          ActionOut();
          break;
          }
        default:
          jj_la1[10] = jj_gen;
          ;
        }
        switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
        case RESULT:{
          ActionResult();
          break;
          }
        default:
          jj_la1[11] = jj_gen;
          ;
        }
        jj_consume_token(RCURLYBRACE);
        break;
        }
      default:
        jj_la1[12] = jj_gen;
        ;
      }
    } catch (Throwable jjte000) {
if (jjtc000) {
        jjtree.clearNodeScope(jjtn000);
        jjtc000 = false;
      } else {
        jjtree.popNode();
      }
      if (jjte000 instanceof RuntimeException) {
        {if (true) throw (RuntimeException)jjte000;}
      }
      if (jjte000 instanceof ParseException) {
        {if (true) throw (ParseException)jjte000;}
      }
      {if (true) throw (Error)jjte000;}
    } finally {
if (jjtc000) {
        jjtree.closeNodeScope(jjtn000, true);
      }
    }
}

  static final public void Refrain() throws ParseException {/*@bgen(jjtree) Refrain */
  ASTRefrain jjtn000 = new ASTRefrain(JJTREFRAIN);
  boolean jjtc000 = true;
  jjtree.openNodeScope(jjtn000);
    try {
      jj_consume_token(REFRAIN);
    } finally {
if (jjtc000) {
        jjtree.closeNodeScope(jjtn000, true);
      }
    }
}

  static final public void RefrainPolicy() throws ParseException {/*@bgen(jjtree) RefrainPolicy */
  ASTRefrainPolicy jjtn000 = new ASTRefrainPolicy(JJTREFRAINPOLICY);
  boolean jjtc000 = true;
  jjtree.openNodeScope(jjtn000);
    try {
      Refrain();
      PolicyName();
      jj_consume_token(LCURLYBRACE);
      Subject();
      Target();
      jj_consume_token(ACTION);
      ActionItem();
      jj_consume_token(SEMICOLON);
      jj_consume_token(RCURLYBRACE);
    } catch (Throwable jjte000) {
if (jjtc000) {
        jjtree.clearNodeScope(jjtn000);
        jjtc000 = false;
      } else {
        jjtree.popNode();
      }
      if (jjte000 instanceof RuntimeException) {
        {if (true) throw (RuntimeException)jjte000;}
      }
      if (jjte000 instanceof ParseException) {
        {if (true) throw (ParseException)jjte000;}
      }
      {if (true) throw (Error)jjte000;}
    } finally {
if (jjtc000) {
        jjtree.closeNodeScope(jjtn000, true);
      }
    }
}

  static final public void PolicyStatement() throws ParseException {/*@bgen(jjtree) PolicyStatement */
  ASTPolicyStatement jjtn000 = new ASTPolicyStatement(JJTPOLICYSTATEMENT);
  boolean jjtc000 = true;
  jjtree.openNodeScope(jjtn000);
    try {
      Instance();
      switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
      case REFRAIN:{
        RefrainPolicy();
        break;
        }
      case AUTHPlUS:
      case AUTHMINUS:{
        AuthPolicy();
        break;
        }
      default:
        jj_la1[13] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    } catch (Throwable jjte000) {
if (jjtc000) {
        jjtree.clearNodeScope(jjtn000);
        jjtc000 = false;
      } else {
        jjtree.popNode();
      }
      if (jjte000 instanceof RuntimeException) {
        {if (true) throw (RuntimeException)jjte000;}
      }
      if (jjte000 instanceof ParseException) {
        {if (true) throw (ParseException)jjte000;}
      }
      {if (true) throw (Error)jjte000;}
    } finally {
if (jjtc000) {
        jjtree.closeNodeScope(jjtn000, true);
      }
    }
}

  static private boolean jj_initialized_once = false;
  /** Generated Token Manager. */
  static public PonderTokenManager token_source;
  static SimpleCharStream jj_input_stream;
  /** Current token. */
  static public Token token;
  /** Next token. */
  static public Token jj_nt;
  static private int jj_ntk;
  static private int jj_gen;
  static final private int[] jj_la1 = new int[14];
  static private int[] jj_la1_0;
  static private int[] jj_la1_1;
  static {
	   jj_la1_init_0();
	   jj_la1_init_1();
	}
	private static void jj_la1_init_0() {
	   jj_la1_0 = new int[] {0x80,0x20000,0x2000000,0x2000000,0x18000000,0x2000,0x300,0x200000,0x400000,0x20000,0x400000,0x800000,0x100000,0x1000300,};
	}
	private static void jj_la1_init_1() {
	   jj_la1_1 = new int[] {0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,};
	}

  /** Constructor with InputStream. */
  public Ponder(InputStream stream) {
	  this(stream, null);
  }
  /** Constructor with InputStream and supplied encoding */
  public Ponder(InputStream stream, String encoding) {
	 if (jj_initialized_once) {
	   System.out.println("ERROR: Second call to constructor of static parser.  ");
	   System.out.println("	   You must either use ReInit() or set the JavaCC option STATIC to false");
	   System.out.println("	   during parser generation.");
	   throw new Error();
	 }
	 jj_initialized_once = true;
	 try { jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1); } catch(UnsupportedEncodingException e) { throw new RuntimeException(e); }
	 token_source = new PonderTokenManager(jj_input_stream);
	 token = new Token();
	 jj_ntk = -1;
	 jj_gen = 0;
	 for (int i = 0; i < 14; i++) jj_la1[i] = -1;
  }

  /** Reinitialise. */
  static public void ReInit(InputStream stream) {
	  ReInit(stream, null);
  }
  /** Reinitialise. */
  static public void ReInit(InputStream stream, String encoding) {
	 try { jj_input_stream.ReInit(stream, encoding, 1, 1); } catch(UnsupportedEncodingException e) { throw new RuntimeException(e); }
	 token_source.ReInit(jj_input_stream);
	 token = new Token();
	 jj_ntk = -1;
	 jjtree.reset();
	 jj_gen = 0;
	 for (int i = 0; i < 14; i++) jj_la1[i] = -1;
  }

  /** Constructor. */
  public Ponder(Reader stream) {
	 if (jj_initialized_once) {
	   System.out.println("ERROR: Second call to constructor of static parser. ");
	   System.out.println("	   You must either use ReInit() or set the JavaCC option STATIC to false");
	   System.out.println("	   during parser generation.");
	   throw new Error();
	 }
	 jj_initialized_once = true;
	 jj_input_stream = new SimpleCharStream(stream, 1, 1);
	 token_source = new PonderTokenManager(jj_input_stream);
	 token = new Token();
	 jj_ntk = -1;
	 jj_gen = 0;
	 for (int i = 0; i < 14; i++) jj_la1[i] = -1;
  }

  /** Reinitialise. */
  static public void ReInit(Reader stream) {
	if (jj_input_stream == null) {
	   jj_input_stream = new SimpleCharStream(stream, 1, 1);
	} else {
	   jj_input_stream.ReInit(stream, 1, 1);
	}
	if (token_source == null) {
 token_source = new PonderTokenManager(jj_input_stream);
	}

	 token_source.ReInit(jj_input_stream);
	 token = new Token();
	 jj_ntk = -1;
	 jjtree.reset();
	 jj_gen = 0;
	 for (int i = 0; i < 14; i++) jj_la1[i] = -1;
  }

  /** Constructor with generated Token Manager. */
  public Ponder(PonderTokenManager tm) {
	 if (jj_initialized_once) {
	   System.out.println("ERROR: Second call to constructor of static parser. ");
	   System.out.println("	   You must either use ReInit() or set the JavaCC option STATIC to false");
	   System.out.println("	   during parser generation.");
	   throw new Error();
	 }
	 jj_initialized_once = true;
	 token_source = tm;
	 token = new Token();
	 jj_ntk = -1;
	 jj_gen = 0;
	 for (int i = 0; i < 14; i++) jj_la1[i] = -1;
  }

  /** Reinitialise. */
  public void ReInit(PonderTokenManager tm) {
	 token_source = tm;
	 token = new Token();
	 jj_ntk = -1;
	 jjtree.reset();
	 jj_gen = 0;
	 for (int i = 0; i < 14; i++) jj_la1[i] = -1;
  }

  static private Token jj_consume_token(int kind) throws ParseException {
	 Token oldToken;
	 if ((oldToken = token).next != null) token = token.next;
	 else token = token.next = token_source.getNextToken();
	 jj_ntk = -1;
	 if (token.kind == kind) {
	   jj_gen++;
	   return token;
	 }
	 token = oldToken;
	 jj_kind = kind;
	 throw generateParseException();
  }


/** Get the next Token. */
  static final public Token getNextToken() {
	 if (token.next != null) token = token.next;
	 else token = token.next = token_source.getNextToken();
	 jj_ntk = -1;
	 jj_gen++;
	 return token;
  }

/** Get the specific Token. */
  static final public Token getToken(int index) {
	 Token t = token;
	 for (int i = 0; i < index; i++) {
	   if (t.next != null) t = t.next;
	   else t = t.next = token_source.getNextToken();
	 }
	 return t;
  }

  static private int jj_ntk_f() {
	 if ((jj_nt=token.next) == null)
	   return (jj_ntk = (token.next=token_source.getNextToken()).kind);
	 else
	   return (jj_ntk = jj_nt.kind);
  }

  static private java.util.List<int[]> jj_expentries = new java.util.ArrayList<int[]>();
  static private int[] jj_expentry;
  static private int jj_kind = -1;

  /** Generate ParseException. */
  static public ParseException generateParseException() {
	 jj_expentries.clear();
	 boolean[] la1tokens = new boolean[33];
	 if (jj_kind >= 0) {
	   la1tokens[jj_kind] = true;
	   jj_kind = -1;
	 }
	 for (int i = 0; i < 14; i++) {
	   if (jj_la1[i] == jj_gen) {
		 for (int j = 0; j < 32; j++) {
		   if ((jj_la1_0[i] & (1<<j)) != 0) {
			 la1tokens[j] = true;
		   }
		   if ((jj_la1_1[i] & (1<<j)) != 0) {
			 la1tokens[32+j] = true;
		   }
		 }
	   }
	 }
	 for (int i = 0; i < 33; i++) {
	   if (la1tokens[i]) {
		 jj_expentry = new int[1];
		 jj_expentry[0] = i;
		 jj_expentries.add(jj_expentry);
	   }
	 }
	 int[][] exptokseq = new int[jj_expentries.size()][];
	 for (int i = 0; i < jj_expentries.size(); i++) {
	   exptokseq[i] = jj_expentries.get(i);
	 }
	 return new ParseException(token, exptokseq, tokenImage);
  }

  static private boolean trace_enabled;

/** Trace enabled. */
  static final public boolean trace_enabled() {
	 return trace_enabled;
  }

  /** Enable tracing. */
  static final public void enable_tracing() {
  }

  /** Disable tracing. */
  static final public void disable_tracing() {
  }

}
