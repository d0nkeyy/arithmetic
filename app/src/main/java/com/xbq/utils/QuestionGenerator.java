package com.xbq.utils;

import com.xbq.Question;

import java.util.ArrayList;
import java.util.Random;

public class QuestionGenerator {
    public QuestionGenerator() {
    }
    public ArrayList<Question> generate() {
        ArrayList<Question> questions = new ArrayList<>();
        BinaryTree binaryTree;
        for (int i = 0; i < ConfigConstants.MAX_QUESTION_NUM; i++) {
            binaryTree = new BinaryTree(2);
            binaryTree.createBTree();
            String result = binaryTree.CalAndVal();
            Question question = new Question();
            question.setDetail(binaryTree.toString() + "=");
            question.setRightAnswer(result);
            questions.add(question);
        }

        return questions;
    }
}

class Ran {

    /**
     * 获取随机的符号
     *
     * @return operator
     */

    public static char getOperator(){
        char operator = 0;
        Random ran = new Random();
        int i = ran.nextInt(4);
        switch(i){
            case 0:
                operator = '+';
                break;
            case 1:
                operator = '-';
                break;
            case 2:
                operator = '*';
                break;
            case 3:
                operator = '/';
                break;
        }
        return operator;
    }


    /**
     * 根据输入的范围获取随机数
     *
     * @param max
     * @return number
     */

    public static int getNumber(int max){
        int number = 0;
        Random ran = new Random();
        number = ran.nextInt(max+1);
        return number;
    }

    /**
     * 根据运算符的个数随机产生子节点的位置
     *
     * @param num
     * @return childPlace
     */

    public static boolean[] getChildPlace(int num){
        int d = 0;
        int size = 0, j=1;
        while(num >= (int)Math.pow(2, j)){
            j++;
        }
        d = (int)Math.pow(2, j) - 1 - num;
        size = (int)Math.pow(2, j-1);
        boolean[] k = new boolean[size];
        for(int i = 0; i < size; i++){
            k[i] = true;
        }
        for(int i = 0; i < d; i++){
            Random ran = new Random();
            int f = ran.nextInt(size);
            while(k[f] == false)
            {
                f = ran.nextInt(size);
            }
            k[f] = false;
        }
        return k;
    }
}

class TreeNode {

    private String str;
    private TreeNode rchild = null;
    private TreeNode lchild = null;

    public TreeNode(String str){
        this.str = str;
    }

    public TreeNode(String str, TreeNode lchild, TreeNode rchild){
        this.str = str;
        this.rchild = rchild;
        this.lchild = lchild;
    }

    public void setChild(TreeNode lchild, TreeNode rchild){
        this.lchild = lchild;
        this.rchild = rchild;
    }

    public TreeNode getRchild() {
        return rchild;
    }
    public void setRchild(TreeNode rchild) {
        this.rchild = rchild;
    }
    public TreeNode getLchild() {
        return lchild;
    }
    public void setLchild(TreeNode lchild) {
        this.lchild = lchild;
    }

    public String getStr(){
        return str;
    }

    /**
     * 获取每个节点的运算结果，并检验除法
     * 1)除数为0
     * 2)不能整除
     * 出现以上两种情况的话将该运算符转换成其他三种运算符
     *
     * @return result
     */
    public String getResult(){
        if(hasChild()){
            switch(str){
                case "+":
                    return String.valueOf(Integer.parseInt(getLchild().getResult()) + Integer.parseInt(getRchild().getResult()));
                case "-":
                    return String.valueOf(Integer.parseInt(getLchild().getResult()) - Integer.parseInt(getRchild().getResult()));
                case "*":
                    return String.valueOf(Integer.parseInt(getLchild().getResult()) * Integer.parseInt(getRchild().getResult()));
                case "/":
                    if(getRchild().getResult().equals("0")){
                        while(str.equals("/")){
                            str = String.valueOf(Ran.getOperator());
                        }
                        return this.getResult();
                    }
                    else if(Integer.parseInt(getLchild().getResult()) % Integer.parseInt(getRchild().getResult()) != 0){
                        while(str.equals("/")){
                            str = String.valueOf(Ran.getOperator());
                        }
                        return this.getResult();
                    }
                    else
                        return String.valueOf(Integer.parseInt(getLchild().getResult()) / Integer.parseInt(getRchild().getResult()));
            }
        }
        return str;
    }

    /**
     * 先对每个运算式添加括号，然后根据去括号法则，去掉多余的子式的括号
     *
     * @return string
     */
    public String toString(){
        String Lstr = "", Rstr = "", Str = "";
        if(hasChild()){
            //右子树如果有孩子，说明右子树是一个表达式，而不是数字节点。
            if(getRchild().hasChild()){
                //判断左邻括号的运算符是否为'/'
                if(str.equals("/")){
                    //获取右子树的表达式，保留括号
                    Rstr = getRchild().toString();
                }
                //判断左邻括号的运算符是否为'*'或'-'
                else if(str.equals("*") || str.equals("-")){
                    //判断op是否为'+'或'-'
                    if(getRchild().str.equals("+") || getRchild().str.equals("-")){
                        Rstr = getRchild().toString();
                    }
                    else{
                        //获取右子树的表达式，并且去括号
                        Rstr = getRchild().toString().substring(1, getRchild().toString().length()-1);
                    }
                }
                else{
                    //右子树除此之外都是可以去括号的。
                    Rstr = getRchild().toString().substring(1, getRchild().toString().length()-1);
                }
            }
            else{
                Rstr = getRchild().str;
            }
            //左子树的情况同右子树类似
            if(getLchild().hasChild()){
                if(str.equals("*") || str.equals("/")){
                    if(getLchild().str.equals("+") || getLchild().str.equals("-")){
                        Lstr = getLchild().toString();
                    }
                    else{
                        Lstr = getLchild().toString().substring(1, getLchild().toString().length()-1);
                    }
                }
                else{
                    Lstr = getLchild().toString().substring(1, getLchild().toString().length()-1);
                }
            }
            else{
                Lstr = getLchild().str;
            }
            //获取当前的运算式，并加上括号
            Str = "(" + Lstr + str + Rstr + ")";
        }
        else{
            //若没有孩子，说明是数字节点，直接返回数字
            Str = str;
        }
        return Str;
    }

    public boolean hasChild(){
        if(lchild == null && rchild == null)
            return false;
        else
            return true;
    }
}

class BinaryTree
{

    private TreeNode root;
    private int num;
    private ArrayList<TreeNode> opeList = new ArrayList<TreeNode>();

    public BinaryTree(int num){
        this.num = num;
    }

    public int getNum(){
        return num;
    }

    public void setNum(int num){
        this.num = num;
    }

    public void setTreeNode(TreeNode root){
        this.root = root;
    }


    /**
     * 获取最终的表达式，必须在CalAndVal()方法后调用
     *
     * @return str
     */
    public String toString(){
        String str = root.toString();
        str = str.substring(1, str.length()-1);
        return str;
    }

    /**
     * 计算并验证表达式
     *
     * @return result
     */
    public String CalAndVal(){
        return root.getResult();
    }

    /**
     * 计算二叉树的深度(层数)
     *
     * @return deep
     */
    public int getDeep(){
        int i = this.num;
        int deep = 2;
        while(i/2 > 0){
            deep++;
            i /= 2;
        }
        return deep;
    }

    /**
     * 生成二叉树
     *
     */
    public void createBTree(){
        TreeNode lchild, rchild, lnode, rnode;

        if(num == 1){
            lchild = new TreeNode(String.valueOf(Ran.getNumber(100)), null, null);
            rchild = new TreeNode(String.valueOf(Ran.getNumber(100)), null, null);
            root = new TreeNode(String.valueOf(Ran.getOperator()), lchild, rchild);
        }
        else{
            int num1 = 0;
            int n = getDeep() - 3;
            boolean[] place = Ran.getChildPlace(num);
            root = new TreeNode(String.valueOf(Ran.getOperator()), null, null);
            opeList.add(root);

            for(int i = 0; i < n; i++){
                for(int j = 0; j < (int)Math.pow(2, i); j++, num1++){
                    lchild = new TreeNode(String.valueOf(Ran.getOperator()), null, null);
                    rchild = new TreeNode(String.valueOf(Ran.getOperator()), null, null);
                    opeList.get(j + num1).setChild(lchild, rchild);
                    opeList.add(lchild);
                    opeList.add(rchild);
                }
            }

            for(int i = 0; i < place.length; i++){
                if(place[i]){
                    lnode  = new TreeNode(String.valueOf(Ran.getNumber(100)), null, null);
                    rnode  = new TreeNode(String.valueOf(Ran.getNumber(100)), null, null);
                    if(i%2 == 0){
                        lchild = new TreeNode(String.valueOf(Ran.getOperator()), lnode, rnode);
                        opeList.add(lchild);
                        opeList.get(num1).setLchild(lchild);
                    }
                    else{
                        rchild = new TreeNode(String.valueOf(Ran.getOperator()), lnode, rnode);
                        opeList.add(rchild);
                        opeList.get(num1).setRchild(rchild);
                    }
                }
                else{
                    if(i%2 == 0){
                        lchild = new TreeNode(String.valueOf(Ran.getNumber(100)), null, null);
                        opeList.get(num1).setLchild(lchild);
                    }
                    else{

                        rchild = new TreeNode(String.valueOf(Ran.getNumber(100)), null, null);
                        opeList.get(num1).setRchild(rchild);
                    }
                }
                num1 = num1 + i%2;
            }
        }
    }
}
