use std::ops::Deref;

use jni::JNIEnv;
use jni::objects::{JClass, JObject, JValueGen, JString};

use ast::Node;

mod ast;

struct JavaASTObject<'a> {
    pub object: Box<JObject<'a>>,
    pub scan_stage: i8,
    pub data: Node
}
impl<'a> JavaASTObject<'a> {
    pub fn new(object: JObject<'a>) -> Self {
        Self {
            object: Box::new(object),
            scan_stage: 0,
            data: Node::Placeholder
        }
    }
}

static mut CLASSES: Vec<Node> = Vec::new();

#[no_mangle]
pub unsafe extern "system" fn Java_org_j2c_llvm_LLVM_createAST<'a: 'static>(mut env: JNIEnv<'a>, _: JClass<'a>, root: JObject<'a>) {
    let mut root = JavaASTObject::new(root);
    
    let mut last_result = None;
    while last_result == None {
        last_result = parse_nclass(&mut env, &mut root);
    }
    println!("{:#?}", root.data);
    CLASSES.push(root.data);
}
#[no_mangle]
pub unsafe extern "system" fn Java_org_j2c_llvm_LLVM_compileCurrentAST() {
    println!("Finishing code generation");
    CLASSES.clear();
}

fn parse_nclass<'a>(env: &mut JNIEnv<'a>, object: &mut JavaASTObject<'a>) -> Option<Node> {
    if object.scan_stage == 0 {
        let name_field = env.get_field(object.object.deref(), "name", "Ljava/lang/String;").unwrap().l().unwrap();
        let name: String = env.get_string(&JString::from(name_field)).unwrap().into();
        object.data = Node::NClass {
            name,
            fields: Vec::new(),
            methods: Vec::new()
        };
        object.scan_stage += 1;
    } else if object.scan_stage == 1 {
        let size = env.call_method(object.object.deref(), "numFields", "()I", &[]).unwrap().i().unwrap();
        let mut temp_data = object.data.clone();
        match object.data.clone() {
            Node::NClass { fields, .. } => {
                if fields.len() == size.try_into().unwrap() {
                    object.scan_stage += 1;
                } else {
                    let next_field = env.call_method(object.object.deref(), "getField", "(I)Lorg/j2c/ast/NFieldDeclaration;", &[JValueGen::Int(fields.len().try_into().unwrap())]).unwrap().l().unwrap();
                    let mut field = JavaASTObject::new(next_field);

                    let mut last_result = None;
                    while last_result == None {
                        last_result = parse_nfielddeclaration(env, &mut field);
                    }
                    match temp_data {
                        Node::NClass { ref mut fields, .. } => {
                            fields.push(last_result.unwrap().clone());
                        },
                        _ => panic!("NClass data isn't an NClass!")
                    };
                }
            },
            _ => panic!("NClass data isn't an NClass!")
        };
        object.data = temp_data;
    } else if object.scan_stage == 2 {
        let size = env.call_method(object.object.deref(), "numMethods", "()I", &[]).unwrap().i().unwrap();
        let mut temp_data = object.data.clone();
        match object.data.clone() {
            Node::NClass { methods, .. } => {
                if methods.len() == size.try_into().unwrap() {
                    object.scan_stage += 1;
                } else {
                    let next_method = env.call_method(object.object.deref(), "getMethod", "(I)Lorg/j2c/ast/NMethodDeclaration;", &[JValueGen::Int(methods.len().try_into().unwrap())]).unwrap().l().unwrap();
                    let mut method = JavaASTObject::new(next_method);

                    let mut last_result = None;
                    while last_result == None {
                        last_result = parse_nmethoddeclaration(env, &mut method);
                    }
                    match temp_data {
                        Node::NClass { ref mut methods, .. } => {
                            methods.push(last_result.unwrap().clone());
                        },
                        _ => panic!("NClass data isn't an NClass!")
                    };
                }
            },
            _ => panic!("NClass data isn't an NClass!")
        };
        object.data = temp_data;
    } else {
        return Some(object.data.clone());
    }
    None
}
fn parse_nfielddeclaration<'a>(env: &mut JNIEnv<'a>, object: &mut JavaASTObject<'a>) -> Option<Node> {
    if object.scan_stage == 0 {
        let name_field = env.get_field(object.object.deref(), "name", "Ljava/lang/String;").unwrap().l().unwrap();
        let name: String = env.get_string(&JString::from(name_field)).unwrap().into();
        let type_field = env.get_field(object.object.deref(), "type", "Ljava/lang/String;").unwrap().l().unwrap();
        let value_type: String = env.get_string(&JString::from(type_field)).unwrap().into();
        object.data = Node::NFieldDeclaration {
            name,
            value_type
        };
        object.scan_stage += 1;
    } else {
        return Some(object.data.clone());
    }
    None
}
fn parse_nmethoddeclaration<'a>(env: &mut JNIEnv<'a>, object: &mut JavaASTObject<'a>) -> Option<Node> {
    if object.scan_stage == 0 {
        let name_field = env.get_field(object.object.deref(), "name", "Ljava/lang/String;").unwrap().l().unwrap();
        let name: String = env.get_string(&JString::from(name_field)).unwrap().into();
        let ret_field = env.get_field(object.object.deref(), "ret", "Ljava/lang/String;").unwrap().l().unwrap();
        let ret: String = env.get_string(&JString::from(ret_field)).unwrap().into();
        object.data = Node::NMethodDeclaration {
            name,
            ret,
            args: Vec::new(),
            body: Vec::new()
        };
        object.scan_stage += 1;
    } else if object.scan_stage == 1 {
        let size = env.call_method(object.object.deref(), "numArgs", "()I", &[]).unwrap().i().unwrap();
        let mut temp_data = object.data.clone();
        match object.data.clone() {
            Node::NMethodDeclaration { args, .. } => {
                if args.len() == size.try_into().unwrap() {
                    object.scan_stage += 1;
                } else {
                    let next_arg_jvalue = env.call_method(object.object.deref(), "getArg", "(I)Ljava/lang/String;", &[JValueGen::Int(args.len().try_into().unwrap())]).unwrap().l().unwrap();
                    let next_arg: String = env.get_string(&JString::from(next_arg_jvalue)).unwrap().into();
                    match temp_data {
                        Node::NMethodDeclaration { ref mut args, .. } => {
                            args.push(next_arg);
                        },
                        _ => panic!("NMethodDeclaration data isn't an NMethodDeclaration!")
                    }
                }
            },
            _ => panic!("NMethodDeclaration data isn't an NMethodDeclaration!")
        };
        object.data = temp_data;
    } else if object.scan_stage == 2 {
        let size = env.call_method(object.object.deref(), "bodySize", "()I", &[]).unwrap().i().unwrap();
        let mut temp_data = object.data.clone();
        match object.data.clone() {
            Node::NMethodDeclaration { body, .. } => {
                if body.len() == size.try_into().unwrap() {
                    object.scan_stage += 1;
                } else {
                    let next_node = env.call_method(object.object.deref(), "getBodyElement", "(I)Lorg/j2c/ast/Node;", &[JValueGen::Int(body.len().try_into().unwrap())]).unwrap().l().unwrap();
                    let mut node = JavaASTObject::new(next_node);

                    let last_result = parse_node(env, &mut node);
                    match temp_data {
                        Node::NMethodDeclaration { ref mut body, .. } => {
                            body.push(last_result.unwrap().clone());
                        },
                        _ => panic!("NMethodDeclaration data isn't an NMethodDeclaration!")
                    }
                }
            },
            _ => panic!("NMethodDeclaration data isn't an NMethodDeclaration!")
        };
        object.data = temp_data;
    } else {
        return Some(object.data.clone());
    }
    None
}
fn parse_node<'a>(env: &mut JNIEnv<'a>, object: &mut JavaASTObject<'a>) -> Option<Node> {
    let ast_name_field = env.get_field(object.object.deref(), "astName", "Ljava/lang/String;").unwrap().l().unwrap();
    let ast_name: String = env.get_string(&JString::from(ast_name_field)).unwrap().into();

    let mut last_result = None;
    while last_result == None {
        match ast_name.as_str() {
            "NBoolean" => {
                last_result = parse_nboolean(env, object);
            },
            "NByte" => {
                last_result = parse_nbyte(env, object);
            },
            "NShort" => {
                last_result = parse_nshort(env, object);
            },
            "NInt" => {
                last_result = parse_nint(env, object);
            },
            "NLong" => {
                last_result = parse_nlong(env, object);
            },
            "NFloat" => {
                last_result = parse_nfloat(env, object);
            },
            "NDouble" => {
                last_result = parse_ndouble(env, object);
            },
            "NNull" => {
                last_result = parse_nnull(env, object);
            },
            "NReference" => {
                last_result = parse_nreference(env, object);
            },
            "NAssignment" => {
                last_result = parse_nassignment(env, object);
            },
            "NStaticReference" => {
                last_result = parse_nstaticreference(env, object);
            },
            "NStaticAssignment" => {
                last_result = parse_nstaticassignment(env, object);
            },
            "NBoundReference" => {
                last_result = parse_nboundreference(env, object);
            },
            "NBoundAssignment" => {
                last_result = parse_nboundassignment(env, object);
            },
            "NStaticCall" => {
                last_result = parse_nstaticcall(env, object);
            },
            "NCall" => {
                last_result = parse_ncall(env, object);
            },
            "NNew" => {
                last_result = parse_nnew(env, object);
            },
            "NBinOp" => {
                last_result = parse_nbinop(env, object);
            },
            "NArrayLength" => {
                last_result = parse_narraylength(env, object);
            },
            "NReturn" => {
                last_result = parse_nreturn(env, object);
            },
            "NValueReturn" => {
                last_result = parse_nvaluereturn(env, object);
            },
            "NAThrow" => {
                last_result = parse_nathrow(env, object);
            },
            "NIf" => {
                last_result = parse_nif(env, object);
            },
            "NOther" => {
                last_result = parse_nother(env, object);
            },
            thing => panic!("Unknown astName: {}", thing)
        }
    }
    last_result
}
fn parse_nboolean<'a>(env: &mut JNIEnv<'a>, object: &mut JavaASTObject<'a>) -> Option<Node> {
    let value = env.get_field(object.object.deref(), "value", "Z").unwrap().z().unwrap();
    Some(Node::NBoolean { value })
}
fn parse_nbyte<'a>(env: &mut JNIEnv<'a>, object: &mut JavaASTObject<'a>) -> Option<Node> {
    let value = env.get_field(object.object.deref(), "value", "B").unwrap().b().unwrap();
    Some(Node::NByte { value })
}
fn parse_nshort<'a>(env: &mut JNIEnv<'a>, object: &mut JavaASTObject<'a>) -> Option<Node> {
    let value = env.get_field(object.object.deref(), "value", "S").unwrap().s().unwrap();
    Some(Node::NShort { value })
}
fn parse_nint<'a>(env: &mut JNIEnv<'a>, object: &mut JavaASTObject<'a>) -> Option<Node> {
    let value = env.get_field(object.object.deref(), "value", "I").unwrap().i().unwrap();
    Some(Node::NInt { value })
}
fn parse_nlong<'a>(env: &mut JNIEnv<'a>, object: &mut JavaASTObject<'a>) -> Option<Node> {
    let value = env.get_field(object.object.deref(), "value", "J").unwrap().j().unwrap();
    Some(Node::NLong { value })
}
fn parse_nfloat<'a>(env: &mut JNIEnv<'a>, object: &mut JavaASTObject<'a>) -> Option<Node> {
    let value = env.get_field(object.object.deref(), "value", "F").unwrap().f().unwrap();
    Some(Node::NFloat { value })
}
fn parse_ndouble<'a>(env: &mut JNIEnv<'a>, object: &mut JavaASTObject<'a>) -> Option<Node> {
    let value = env.get_field(object.object.deref(), "value", "D").unwrap().d().unwrap();
    Some(Node::NDouble { value })
}
fn parse_nnull<'a>(_: &mut JNIEnv<'a>, _: &mut JavaASTObject<'a>) -> Option<Node> {
    Some(Node::NNull)
}
fn parse_nreference<'a>(env: &mut JNIEnv<'a>, object: &mut JavaASTObject<'a>) -> Option<Node> {
    if object.scan_stage == 0 {
        let identifier_field = env.get_field(object.object.deref(), "identifier", "Ljava/lang/String;").unwrap().l().unwrap();
        let identifier: String = env.get_string(&JString::from(identifier_field)).unwrap().into();
        object.data = Node::NReference {
            identifier
        };
        object.scan_stage += 1;
    } else {
        return Some(object.data.clone());
    }
    None
}
fn parse_nassignment<'a>(env: &mut JNIEnv<'a>, object: &mut JavaASTObject<'a>) -> Option<Node> {
    if object.scan_stage == 0 {
        let dest_field = env.get_field(object.object.deref(), "dest", "Ljava/lang/String;").unwrap().l().unwrap();
        let dest: String = env.get_string(&JString::from(dest_field)).unwrap().into();
        object.data = Node::NAssignment {
            dest,
            v: Box::new(Node::Placeholder)
        };
        object.scan_stage += 1;
    } else if object.scan_stage == 1 {
        let node_jvalue = env.get_field(object.object.deref(), "v", "Lorg/j2c/ast/Node;").unwrap().l().unwrap();
        let mut node = JavaASTObject::new(node_jvalue);

        let last_result = parse_node(env, &mut node);
        let mut temp_data = object.data.clone();
        match temp_data {
            Node::NAssignment { ref mut v, .. } => {
                *v = Box::new(last_result.unwrap().clone());
            },
            _ => panic!("NAssignment data isn't an NAssignment!")
        };
        object.data = temp_data;
        object.scan_stage += 1;
    } else {
        return Some(object.data.clone());
    }
    None
}
fn parse_nstaticreference<'a>(env: &mut JNIEnv<'a>, object: &mut JavaASTObject<'a>) -> Option<Node> {
    if object.scan_stage == 0 {
        let field_field = env.get_field(object.object.deref(), "field", "Ljava/lang/String;").unwrap().l().unwrap();
        let field: String = env.get_string(&JString::from(field_field)).unwrap().into();
        object.data = Node::NStaticReference {
            field
        };
        object.scan_stage += 1;
    } else {
        return Some(object.data.clone());
    }
    None
}
fn parse_nstaticassignment<'a>(env: &mut JNIEnv<'a>, object: &mut JavaASTObject<'a>) -> Option<Node> {
    if object.scan_stage == 0 {
        let field_field = env.get_field(object.object.deref(), "field", "Ljava/lang/String;").unwrap().l().unwrap();
        let field: String = env.get_string(&JString::from(field_field)).unwrap().into();
        object.data = Node::NStaticAssignment {
            field,
            v: Box::new(Node::Placeholder)
        };
        object.scan_stage += 1;
    } else if object.scan_stage == 1 {
        let node_jvalue = env.get_field(object.object.deref(), "v", "Lorg/j2c/ast/Node;").unwrap().l().unwrap();
        let mut node = JavaASTObject::new(node_jvalue);

        let last_result = parse_node(env, &mut node);
        let mut temp_data = object.data.clone();
        match temp_data {
            Node::NStaticAssignment { ref mut v, .. } => {
                *v = Box::new(last_result.unwrap().clone());
            },
            _ => panic!("NStaticAssignment data isn't an NStaticAssignment!")
        };
        object.data = temp_data;
        object.scan_stage += 1;
    } else {
        return Some(object.data.clone());
    }
    None
}
fn parse_nboundreference<'a>(env: &mut JNIEnv<'a>, object: &mut JavaASTObject<'a>) -> Option<Node> {
    if object.scan_stage == 0 {
        let field_field = env.get_field(object.object.deref(), "field", "Ljava/lang/String;").unwrap().l().unwrap();
        let field: String = env.get_string(&JString::from(field_field)).unwrap().into();
        object.data = Node::NBoundReference {
            obj: Box::new(Node::Placeholder),
            field
        };
        object.scan_stage += 1;
    } else if object.scan_stage == 1 {
        let node_jvalue = env.get_field(object.object.deref(), "obj", "Lorg/j2c/ast/Node;").unwrap().l().unwrap();
        let mut node = JavaASTObject::new(node_jvalue);

        let last_result = parse_node(env, &mut node);
        let mut temp_data = object.data.clone();
        match temp_data {
            Node::NBoundReference { ref mut obj, .. } => {
                *obj = Box::new(last_result.unwrap().clone());
            },
            _ => panic!("NBoundReference data isn't an NBoundReference!")
        };
        object.data = temp_data;
        object.scan_stage += 1;
    } else {
        return Some(object.data.clone());
    }
    None
}
fn parse_nboundassignment<'a>(env: &mut JNIEnv<'a>, object: &mut JavaASTObject<'a>) -> Option<Node> {
    if object.scan_stage == 0 {
        let field_field = env.get_field(object.object.deref(), "field", "Ljava/lang/String;").unwrap().l().unwrap();
        let field: String = env.get_string(&JString::from(field_field)).unwrap().into();
        object.data = Node::NBoundAssignment {
            obj: Box::new(Node::Placeholder),
            dest: field,
            v: Box::new(Node::Placeholder)
        };
        object.scan_stage += 1;
    } else if object.scan_stage == 1 {
        let node_jvalue = env.get_field(object.object.deref(), "obj", "Lorg/j2c/ast/Node;").unwrap().l().unwrap();
        let mut node = JavaASTObject::new(node_jvalue);

        let last_result = parse_node(env, &mut node);
        let mut temp_data = object.data.clone();
        match temp_data {
            Node::NBoundAssignment { ref mut obj, .. } => {
                *obj = Box::new(last_result.unwrap().clone());
            },
            _ => panic!("NBoundAssignment data isn't an NBoundAssignment!")
        };
        object.data = temp_data;
        object.scan_stage += 1;
    } else if object.scan_stage == 2 {
        let node_jvalue = env.get_field(object.object.deref(), "v", "Lorg/j2c/ast/Node;").unwrap().l().unwrap();
        let mut node = JavaASTObject::new(node_jvalue);

        let last_result = parse_node(env, &mut node);
        let mut temp_data = object.data.clone();
        match temp_data {
            Node::NBoundAssignment { ref mut v, .. } => {
                *v = Box::new(last_result.unwrap().clone());
            },
            _ => panic!("NBoundAssignment data isn't an NBoundAssignment!")
        };
        object.data = temp_data;
        object.scan_stage += 1;
    } else {
        return Some(object.data.clone());
    }
    None
}
fn parse_nstaticcall<'a>(env: &mut JNIEnv<'a>, object: &mut JavaASTObject<'a>) -> Option<Node> {
    if object.scan_stage == 0 {
        let method_field = env.get_field(object.object.deref(), "method", "Ljava/lang/String;").unwrap().l().unwrap();
        let method: String = env.get_string(&JString::from(method_field)).unwrap().into();
        object.data = Node::NStaticCall {
            method,
            args: Vec::new()
        };
        object.scan_stage += 1;
    } else if object.scan_stage == 1 {
        let size = env.call_method(object.object.deref(), "numArgs", "()I", &[]).unwrap().i().unwrap();
        let mut temp_data = object.data.clone();
        match object.data.clone() {
            Node::NStaticCall { args, .. } => {
                if args.len() == size.try_into().unwrap() {
                    object.scan_stage += 1;
                } else {
                    let next_arg_jvalue = env.call_method(object.object.deref(), "getArg", "(I)Lorg/j2c/ast/Node;", &[JValueGen::Int(args.len().try_into().unwrap())]).unwrap().l().unwrap();
                    let mut next_arg = JavaASTObject::new(next_arg_jvalue);

                    let last_result = parse_node(env, &mut next_arg);
                    match temp_data {
                        Node::NStaticCall { ref mut args, .. } => {
                            args.push(last_result.unwrap().clone());
                        },
                        _ => panic!("NStaticCall data isn't an NStaticCall!")
                    }
                }
            },
            _ => panic!("NStaticCall data isn't an NStaticCall!")
        };
        object.data = temp_data;
        object.scan_stage += 1;
    } else {
        return Some(object.data.clone());
    }
    None
}
fn parse_ncall<'a>(env: &mut JNIEnv<'a>, object: &mut JavaASTObject<'a>) -> Option<Node> {
    if object.scan_stage == 0 {
        let method_field = env.get_field(object.object.deref(), "method", "Ljava/lang/String;").unwrap().l().unwrap();
        let method: String = env.get_string(&JString::from(method_field)).unwrap().into();
        object.data = Node::NCall {
            obj: Box::new(Node::Placeholder),
            method,
            args: Vec::new()
        };
        object.scan_stage += 1;
    } else if object.scan_stage == 1 {
        let node_jvalue = env.get_field(object.object.deref(), "obj", "Lorg/j2c/ast/Node;").unwrap().l().unwrap();
        let mut node = JavaASTObject::new(node_jvalue);

        let last_result = parse_node(env, &mut node);
        let mut temp_data = object.data.clone();
        match temp_data {
            Node::NCall { ref mut obj, .. } => {
                *obj = Box::new(last_result.unwrap().clone());
            },
            _ => panic!("NCall data isn't an NCall!")
        }
        object.data = temp_data;
        object.scan_stage += 1;
    } else if object.scan_stage == 2 {
        let size = env.call_method(object.object.deref(), "numArgs", "()I", &[]).unwrap().i().unwrap();
        let mut temp_data = object.data.clone();
        match object.data.clone() {
            Node::NCall { args, .. } => {
                if args.len() == size.try_into().unwrap() {
                    object.scan_stage += 1;
                } else {
                    let next_arg_jvalue = env.call_method(object.object.deref(), "getArg", "(I)Lorg/j2c/ast/Node;", &[JValueGen::Int(args.len().try_into().unwrap())]).unwrap().l().unwrap();
                    let mut next_arg = JavaASTObject::new(next_arg_jvalue);

                    let last_result = parse_node(env, &mut next_arg);
                    match temp_data {
                        Node::NCall { ref mut args, .. } => {
                            args.push(last_result.unwrap().clone());
                        },
                        _ => panic!("NCall data isn't an NCall!")
                    }
                }
            },
            _ => panic!("NCall data isn't an NCall!")
        };
        object.data = temp_data;
        object.scan_stage += 1;
    } else {
        return Some(object.data.clone());
    }
    None
}
fn parse_nnew<'a>(env: &mut JNIEnv<'a>, object: &mut JavaASTObject<'a>) -> Option<Node> {
    if object.scan_stage == 0 {
        let clazz_field = env.get_field(object.object.deref(), "clazz", "Ljava/lang/String;").unwrap().l().unwrap();
        let clazz: String = env.get_string(&JString::from(clazz_field)).unwrap().into();
        object.data = Node::NNew {
            class: clazz
        };
        object.scan_stage += 1;
    } else {
        return Some(object.data.clone());
    }
    None
}
fn parse_nbinop<'a>(env: &mut JNIEnv<'a>, object: &mut JavaASTObject<'a>) -> Option<Node> {
    if object.scan_stage == 0 {
        object.data = Node::NBinOp {
            operand_type: String::from(""),
            op: String::from(""),
            left: Box::new(Node::Placeholder),
            right: Box::new(Node::Placeholder)
        };
        object.scan_stage += 1;
    } else if object.scan_stage == 1 {
        let type_field = env.get_field(object.object.deref(), "type", "Ljava/lang/String;").unwrap().l().unwrap();
        let op_type: String = env.get_string(&JString::from(type_field)).unwrap().into();

        let op_field = env.get_field(object.object.deref(), "op", "Ljava/lang/String;").unwrap().l().unwrap();
        let op_value: String = env.get_string(&JString::from(op_field)).unwrap().into();

        let left_field = env.get_field(object.object.deref(), "left", "Lorg/j2c/ast/Node;").unwrap().l().unwrap();
        let mut left = JavaASTObject::new(left_field);
        let left_value = parse_node(env, &mut left);

        let right_field = env.get_field(object.object.deref(), "right", "Lorg/j2c/ast/Node;").unwrap().l().unwrap();
        let mut right = JavaASTObject::new(right_field);
        let right_value = parse_node(env, &mut right);

        let mut temp_data = object.data.clone();
        match temp_data {
            Node::NBinOp { ref mut operand_type, ref mut op, ref mut left, ref mut right } => {
                *operand_type = op_type;
                *op = op_value;
                *left = Box::new(left_value.unwrap().clone());
                *right = Box::new(right_value.unwrap().clone());
            },
            _ => panic!("NDDiv data isn't an NDDiv!")
        }
        object.data = temp_data;
        object.scan_stage += 1;
    } else {
        return Some(object.data.clone());
    }
    None
}
fn parse_narraylength<'a>(env: &mut JNIEnv<'a>, object: &mut JavaASTObject<'a>) -> Option<Node> {
    if object.scan_stage == 0 {
        object.data = Node::NArrayLength {
            array: Box::new(Node::Placeholder)
        };
        object.scan_stage += 1;
    } else if object.scan_stage == 1 {
        let node_jvalue = env.get_field(object.object.deref(), "array", "Lorg/j2c/ast/NArrayLength;").unwrap().l().unwrap();
        let mut node = JavaASTObject::new(node_jvalue);

        let last_result = parse_node(env, &mut node);
        let mut temp_data = object.data.clone();
        match temp_data {
            Node::NArrayLength { ref mut array } => {
                *array = Box::new(last_result.unwrap().clone());
            },
            _ => panic!("NArrayLength data isn't an NArrayLength!")
        };
        object.data = temp_data;
        object.scan_stage += 1;
    } else {
        return Some(object.data.clone());
    }
    None
}
fn parse_nreturn<'a>(_: &mut JNIEnv<'a>, _: &mut JavaASTObject<'a>) -> Option<Node> {
    Some(Node::NReturn)
}
fn parse_nvaluereturn<'a>(env: &mut JNIEnv<'a>, object: &mut JavaASTObject<'a>) -> Option<Node> {
    if object.scan_stage == 0 {
        let type_field = env.get_field(object.object.deref(), "type", "Ljava/lang/String;").unwrap().l().unwrap();
        let return_type: String = env.get_string(&JString::from(type_field)).unwrap().into();
        object.data = Node::NValueReturn {
            return_type,
            v: Box::new(Node::Placeholder)
        };
        object.scan_stage += 1;
    } else if object.scan_stage == 1 {
        let node_jvalue = env.get_field(object.object.deref(), "v", "Lorg/j2c/ast/Node;").unwrap().l().unwrap();
        let mut node = JavaASTObject::new(node_jvalue);

        let last_value = parse_node(env, &mut node);
        let mut temp_data = object.data.clone();
        match temp_data {
            Node::NValueReturn { ref mut v, .. } => {
                *v = Box::new(last_value.unwrap().clone());
            },
            _ => panic!("NValueReturn data isn't an NValueReturn!")
        };
        object.data = temp_data;
        object.scan_stage += 1;
    } else {
        return Some(object.data.clone());
    }
    None
}
fn parse_nathrow<'a>(env: &mut JNIEnv<'a>, object: &mut JavaASTObject<'a>) -> Option<Node> {
    if object.scan_stage == 0 {
        object.data = Node::NAThrow {
            v: Box::new(Node::Placeholder)
        };
        object.scan_stage += 1;
    } else if object.scan_stage == 1 {
        let node_jvalue = env.get_field(object.object.deref(), "v", "Lorg/j2c/ast/Node;").unwrap().l().unwrap();
        let mut node = JavaASTObject::new(node_jvalue);

        let last_value = parse_node(env, &mut node);
        let mut temp_data = object.data.clone();
        match temp_data {
            Node::NAThrow { ref mut v } => {
                *v = Box::new(last_value.unwrap().clone());
            },
            _ => panic!("NAThrow data isn't an NAThrow!")
        };
        object.data = temp_data;
        object.scan_stage += 1;
    } else {
        return Some(object.data.clone());
    }
    None
}
fn parse_nif<'a>(env: &mut JNIEnv<'a>, object: &mut JavaASTObject<'a>) -> Option<Node> {
    if object.scan_stage == 0 {
        let condition_object = env.get_field(object.object.deref(), "condition", "Lorg/j2c/ast/Node;").unwrap().l().unwrap();
        let mut condition = JavaASTObject::new(condition_object);
        let last_result = parse_node(env, &mut condition);
        object.data = Node::NIf {
            condition: Box::new(last_result.unwrap().clone()),
            if_branch: Vec::new(),
            else_branch: Vec::new()
        };
        object.scan_stage += 1;
    } else if object.scan_stage == 1 {
        let size = env.call_method(object.object.deref(), "ifSize", "()I", &[]).unwrap().i().unwrap();
        let mut temp_data = object.data.clone();
        match object.data.clone() {
            Node::NIf { if_branch, .. } => {
                if if_branch.len() == size.try_into().unwrap() {
                    object.scan_stage += 1;
                } else {
                    let next_node = env.call_method(object.object.deref(), "getIfElement", "(I)Lorg/j2c/ast/Node;", &[JValueGen::Int(if_branch.len().try_into().unwrap())]).unwrap().l().unwrap();
                    let mut node = JavaASTObject::new(next_node);

                    let last_result = parse_node(env, &mut node);
                    match temp_data {
                        Node::NIf { ref mut if_branch, .. } => {
                            if_branch.push(last_result.unwrap().clone());
                        },
                        _ => panic!("NIf data isn't an NIf!")
                    }
                }
            },
            _ => panic!("NIf data isn't an NIf!")
        }
        object.data = temp_data;
    } else if object.scan_stage == 2 {
        let size = env.call_method(object.object.deref(), "elseSize", "()I", &[]).unwrap().i().unwrap();
        let mut temp_data = object.data.clone();
        match object.data.clone() {
            Node::NIf { else_branch, .. } => {
                if else_branch.len() == size.try_into().unwrap() {
                    object.scan_stage += 1;
                } else {
                    let next_node = env.call_method(object.object.deref(), "getElseElement", "(I)Lorg/j2c/ast/Node;", &[JValueGen::Int(else_branch.len().try_into().unwrap())]).unwrap().l().unwrap();
                    let mut node = JavaASTObject::new(next_node);

                    let last_result = parse_node(env, &mut node);
                    match temp_data {
                        Node::NIf { ref mut else_branch, .. } => {
                            else_branch.push(last_result.unwrap().clone());
                        },
                        _ => panic!("NIf data isn't an NIf!")
                    }
                }
            },
            _ => panic!("NIf data isn't an NIf!")
        }
        object.data = temp_data;
    } else {
        return Some(object.data.clone());
    }
    None
}
fn parse_nother<'a>(env: &mut JNIEnv<'a>, object: &mut JavaASTObject<'a>) -> Option<Node> {
    if object.scan_stage == 0 {
        let str_field = env.get_field(object.object.deref(), "str", "Ljava/lang/String;").unwrap().l().unwrap();
        let str: String = env.get_string(&JString::from(str_field)).unwrap().into();
        object.data = Node::NOther {
            str
        };
        object.scan_stage += 1;
    } else {
        return Some(object.data.clone());
    }
    None
}