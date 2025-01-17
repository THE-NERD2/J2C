use jni::JNIEnv;
use jni::objects::{JClass, JObject};

use parsing::*;
use ast::Node;

mod ast;
mod parsing;

struct JavaASTObject<'a> {
    pub object: Box<JObject<'a>>,
    pub data: Node
}
impl<'a> JavaASTObject<'a> {
    pub fn new(object: JObject<'a>) -> Self {
        Self {
            object: Box::new(object),
            data: Node::Placeholder
        }
    }
}

static mut CLASSES: Vec<Node> = Vec::new();

#[no_mangle]
pub unsafe extern "system" fn Java_org_j2c_llvm_LLVM_createAST<'a: 'static>(mut env: JNIEnv<'a>, _: JClass<'a>, root: JObject<'a>) {
    let mut root = JavaASTObject::new(root);
    
    parse_nclass(&mut env, &mut root);
    println!("{:#?}", root.data);
    CLASSES.push(root.data);
}
#[no_mangle]
pub unsafe extern "system" fn Java_org_j2c_llvm_LLVM_compileCurrentAST() {
    println!("Finishing code generation");
    CLASSES.clear();
}