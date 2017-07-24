package liangjiahao.dataMining.Utils;

import liangjiahao.dataMining.DataStructure.TreeNode;

import java.util.LinkedList;

public class LevelOrderPrint
{
    public void levelIterator(TreeNode root)
    {
        if(root == null)
        {
            return ;
        }
        LinkedList<TreeNode> queue = new LinkedList<TreeNode>();
        TreeNode current = null;
        queue.offer(root);//将根节点入队
        while(!queue.isEmpty())
        {
            current = queue.poll();//出队队头元素并访问
            System.out.print(current.toString() +"-->");
            for(TreeNode s:current.son)
            if(s != null)//如果当前节点的son节点不为空，把节点入队
                queue.offer(s);

        }

    }

}