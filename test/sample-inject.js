//inject library
load("test/external-lib.js");

//import functions
var ops = new maths.lib();

function operate(op1, op2, oper){
    print(oper.call(this, op1, op2));
};

//apply lib operations
operate(1, 2, ops.sum);

operate(3, 4, ops.multiply);

//apply wrapper around ops
var pops = new JSAdapter(ops) {
    __get__: function(name) {
        print("getter called for '" + name + "'"); 
        return ops[name];
    },
 
    __put__: function(name, value) {
        print("setter called for '" + name + "' with " + value);
    },
 
    __call__: function(name, arg1, arg2) {
        ops[name].call(ops, arg1, arg2);
    },
 
    __new__: function(arg1, arg2) {
        print("new with " + arg1 + ", " + arg2);
    },
 
    __delete__: function(name) {
        print("__delete__ called with '" + name + "'");
        delete ops[name];
        return true;
    }
};

var asserts = org.junit.Assert;

//apply lib operations
operate(1, 2, pops.sum);

operate(3, 4, pops.multiply);