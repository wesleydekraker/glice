<?php

// Define a class with some properties and methods
class MyClass {
    public $public_prop = 42;
    private $private_prop = 'hello';
    protected $protected_prop = true;

    public function __construct($arg1, $arg2) {
        $this->public_prop = $arg1;
        $this->private_prop = $arg2;
    }

    public function public_method($arg) {
        $result = $this->private_method($arg);
        echo "Result: $result\n";
    }

    private function private_method($arg) {
        $value = $this->public_prop + strlen($arg);
        return $value;
    }
}

// Create an object of the class and call its methods
$obj = new MyClass(1, 'world');
$obj->public_method('testing');

// Define a function that uses some control structures
function foo($bar) {
    if ($bar == 1) {
        echo "Bar is one\n";
    } elseif ($bar == 2) {
        echo "Bar is two\n";
    } else {
        echo "Bar is something else\n";
    }

    for ($i = 0; $i < 10; $i++) {
        echo "$i\n";
    }

    $arr = array('one', 'two', 'three');
    foreach ($arr as $value) {
        echo "$value\n";
    }

    $x = 0;
    while ($x < 5) {
        echo "$x\n";
        $x++;
    }

    switch ($bar) {
        case 1:
            echo "Bar is one\n";
            break;
        case 2:
            echo "Bar is two\n";
            break;
        default:
            echo "Bar is something else\n";
            break;
    }
}

// Call the function with different arguments
foo(1);
foo(2);
foo(3);

?>
