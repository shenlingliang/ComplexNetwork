package com.company.util;

import java.io.Serializable;
import java.util.*;

public  class Network implements Serializable {
    private final int size;// 网络大小
    private final List<Node> nodeList;// 节点列表
    private Set<Integer> infectedSet;//感染节点集合
    private  int recoverNodes;//恢复节点数量，疾病传播规模
    int totalFirstLayerDegree=0;//第一层总度数
    int totalSecondLayerDegree=0;//第二层总度数
    int totalFirstLayerStubs=0;// 第一层stubs数量
    int totalSecondLayerStubs=0;// 第二层stubs数量
    Set<Integer> withFirstStubsSet;// 带有第一层stubs的节点集合
    Set<Integer> withSecondStubsSet;// 带有第二层stubs的节点集合

    Random random=new Random();


    public Network(int size,int firstMaxDegree,int firstMinDegree,double firstGamma,int infectedNums,
                   int secondMaxDegree,int secondMinDegree,double secondGamma,double threhold) {
        this.size = size;
        this.nodeList=new ArrayList<>(this.size);
        withFirstStubsSet=new HashSet<>();
        withSecondStubsSet=new HashSet<>();
        doGenNodes(firstMaxDegree,firstMinDegree,firstGamma,secondMaxDegree,secondMinDegree,secondGamma,threhold);
        this.infectedSet=new HashSet<>();
        doInitInfectedNodes(infectedNums);
        this.recoverNodes=0;
//        this.alpha=alpha;
//        this.beta=beta;
//        this.u1=u1;
//        this.u0=u0;
//        this.utilRate=utilRate;
//        this.lambda=lambda;
    }



    /**
    * @Description: 完成节点初始化以及网络连接
    * @Param: [firstMaxDegree, firstMinDegree, firstGamma,secondMaxDegree,secondMinDegree,secondGamma]
    * @return: void
    * @Author: shenll
    * @Date: 2021/5/16
    */
    private void doGenNodes(int firstMaxDegree,int firstMinDegree,double firstGamma,
                            int secondMaxDegree,int secondMinDegree,double secondGamma,double threshold){
        doInitAllNodes();
        doAllocateDegree(firstMaxDegree,firstMinDegree,firstGamma,secondMaxDegree,secondMinDegree,secondGamma);
        link();
        doGenThreshold(threshold);
        check();
    }
    private void check(){
        int fCount=0;
        int sCount=0;
        int [] flist=new int[101];
        int [] slist=new int[101];
        for (Node e:this.nodeList){
            if (e.getFirstStubs()!=e.getFirstDegree()) {
                fCount++;
//                System.out.println(e.getFirstStubs()-e.getFirstDegree());
            }
            if (e.getSecondStubs()!=e.getSecondDegree()) {
                sCount++;
//                System.out.println(e.getSecondStubs()-e.getSecondDegree());
            }
            flist[e.getFirstDegree()]++;
            slist[e.getSecondDegree()]++;
        }
        System.out.println("first diff:"+fCount);
        System.out.println("second diff "+sCount);
        int sumS=0;
        int sumF=0;
        int maxS=0;
        int maxF=0;
//        double totalF=0.0;
//        double totalS=0.0;
        for (int i = 0; i < 101; i++) {
            if (slist[i]>0){
                maxS=Math.max(maxS,i);
                sumS+=slist[i]*i;
            }
            if (flist[i]>0){
                maxF=Math.max(maxF,i);
                sumF+=flist[i]*i;
            }
        }
        System.out.println("first layer max degree :"+maxF);
        System.out.println("ave first :"+sumF/5000.0);
        System.out.println("second layer max degree :"+maxS);
        System.out.println("ave second :"+sumS/5000.0);

    }
    private void doGenThreshold(double threshold){
        for (Node n:nodeList){
            n.setInfectedThreshold((int) Math.ceil(threshold*(n.getFirstDegree()+n.getSecondDegree())/2));
        }
    }

    public void setAllInfectThreshold(double threshold){
        for (Node n:nodeList) {
            n.setInfectedThreshold((int)Math.ceil(n.getFirstLayerNeighbor().size()*threshold));
        }
    }
    /**
    * @Description: 分别根据两层的stubs进行连接
    * @Param: []
    * @return: void
    * @Author: shenll
    * @Date: 2021/5/16
    */
    protected  void link(){
        Random r=new Random();
        // stubs已经分配好了
        int [] kFirstArray=new int[this.totalFirstLayerStubs];
        int firstPos=0;
        int [] kSecondArray=new int[this.totalSecondLayerStubs];
        int secondPos=0;
        for (Node n:nodeList){
            for (int i = 0; i < n.getFirstStubs(); i++) {
                kFirstArray[firstPos]=n.getId();
                firstPos++;
            }
            for (int i = 0; i < n.getSecondStubs(); i++) {
                kSecondArray[secondPos]=n.getId();
                secondPos++;
            }
        }
        int numsFirst=totalFirstLayerStubs;
        int numsSecond=totalSecondLayerStubs;
        while (numsFirst>1){
            int p,q,i,j;
//            boolean flag=false;
            p=r.nextInt(numsFirst);
            q=r.nextInt(numsFirst);
            System.out.println("p="+p);
            System.out.println("q="+q);
            i=kFirstArray[p];
            j=kFirstArray[q];
            if (i!=j){
                Node n=nodeList.get(i);
                if (!n.getFirstLayerNeighbor().getOrDefault(j,false)) // i,j 未相连
                {
                    // 连接
                    Node m=nodeList.get(j);
                    n.getFirstLayerNeighbor().put(j,true);
                    n.setFirstDegree(n.getFirstDegree()+1);
                    m.getFirstLayerNeighbor().put(i,true);
                    m.setFirstDegree(m.getFirstDegree()+1);
                    kFirstArray[p]=kFirstArray[numsFirst-1];
                    kFirstArray[q]=kFirstArray[numsFirst-2];
                    numsFirst-=2;
                }
                System.out.println(numsFirst);
            }
        }
        while (numsSecond>1){
            int p,q,i,j;
//            boolean flag=false;

            p=r.nextInt(numsSecond);
            q=r.nextInt(numsSecond);
            i=kSecondArray[p];
            j=kSecondArray[q];
            if (i!=j){
                Node n=nodeList.get(i);
                if (!n.getSecondLayerNeighbor().getOrDefault(j,false)) // i,j 未相连
                {
                    // 连接
                    Node m=nodeList.get(j);
                    n.getSecondLayerNeighbor().put(j,true);
                    n.setSecondDegree(n.getSecondDegree()+1);
                    m.getSecondLayerNeighbor().put(i,true);
                    m.setSecondDegree(m.getSecondDegree()+1);
                    kSecondArray[p]=kSecondArray[numsSecond-1];
                    kSecondArray[q]=kSecondArray[numsSecond-2];
                    numsSecond-=2;
                }
            }
        }
    }
    private void doLink(){
        // first layer linking
        Random r=new Random();
        for (Node n:nodeList){//遍历每个节点
            int count=0;
            while (n.getFirstStubs()>0){//还有stub
                int tmp=n.getId()+r.nextInt(size-n.getId());//随机抽取一个序号，作为要连接的节点
                Node m=nodeList.get(tmp);
                if (!n.getFirstLayerNeighbor().getOrDefault(tmp,false) && m.getFirstStubs()>0 && n.getId()!=tmp){//如果不是邻居且还有stubs，则连接
                    n.getFirstLayerNeighbor().put(tmp,true);
                    n.setFirstStubs(n.getFirstStubs()-1);
                    m.getFirstLayerNeighbor().put(n.getId(),true);
                    m.setFirstStubs(m.getFirstStubs()-1);
                    totalFirstLayerStubs-=2;
                }
                else {//如果是邻居
                    count++;//计数增加
                    if (count>100){
                        boolean flag=true;
                        for(int k:withFirstStubsSet){
                            Node t=nodeList.get(k);
                            if (!t.equals(n)&& !n.getFirstLayerNeighbor().getOrDefault(k,false) && t.getFirstStubs()>0) {
                                n.getFirstLayerNeighbor().put(k,true);
                                n.setFirstStubs(n.getFirstStubs()-1);
                                t.getFirstLayerNeighbor().put(n.getId(),true);
                                t.setFirstStubs(t.getFirstStubs()-1);
                                totalFirstLayerStubs-=2;
                                flag=false;
                                break;
                            }
                        }//计算剩余可连接的节点
                        if (flag){//没有可连接的节点
                            totalFirstLayerStubs-=n.getFirstStubs();
                            totalFirstLayerDegree-=n.getFirstStubs();
                            n.setFirstDegree(n.getFirstDegree()-n.getFirstStubs());
                            n.setFirstStubs(0);
                        }
                        count=0;
                    }
                }
            }
            withFirstStubsSet.remove(n.getId());
            System.out.println(totalFirstLayerStubs+" left");
        }
        System.out.println("first layer linking finished");
        int sum=0;
        for (Node t: nodeList) sum+=t.getFirstStubs();
        System.out.println(sum);
        for (Node n:nodeList){
            int count=0;
            while (n.getSecondStubs()>0){
//                Random r=new Random(random.nextInt());
                int tmp=n.getId()+r.nextInt(size-n.getId());// 缩小随机范围
                Node m=nodeList.get(tmp);
                if (!n.getSecondLayerNeighbor().getOrDefault(tmp,false)&& m.getSecondStubs()>0 && !n.equals(m)){
                    n.getSecondLayerNeighbor().put(tmp,true);
                    n.setSecondStubs(n.getSecondStubs()-1);
                    m.getSecondLayerNeighbor().put(n.getId(),true);
                    m.setSecondStubs(m.getSecondStubs()-1);
                    totalSecondLayerStubs-=2;
                }
                else {
                    count++;
                    if (count>100){
                        boolean flag=true;
                        for(int k:withSecondStubsSet){
                            Node t=nodeList.get(k);
                            if (k!=n.getId() && !n.getSecondLayerNeighbor().getOrDefault(k,false) && t.getSecondStubs()>0) {
                                n.getSecondLayerNeighbor().put(k,true);
                                n.setSecondStubs(n.getSecondStubs()-1);
                                t.getSecondLayerNeighbor().put(n.getId(),true);
                                t.setSecondStubs(t.getSecondStubs()-1);
                                totalSecondLayerStubs-=2;
                                flag=false;
                                break;
                            }
                        }//计算剩余可连接的节点
                        if (flag){
                            totalSecondLayerStubs-=n.getSecondStubs();
                            totalSecondLayerDegree-=n.getSecondStubs();
                            n.setSecondDegree(n.getSecondDegree()-n.getSecondStubs());
                            n.setSecondStubs(0);
                        }
                        count=0;
                    }
                }
            }
            withSecondStubsSet.remove(n.getId());
            System.out.println(totalSecondLayerStubs+" left");
        }
        System.out.println("Second fins");
    }
    public void reset(){
        this.recoverNodes=0;
        for (Node e:this.nodeList){
            e.setStatus(NodeStatus.SUSPECT);
        }
        setInfectedSet();
    }
    /**
    * @Description: 根据参数计算分布
    * @Param: [firstMaxDegree, firstMinDegree, firstGamma]
    * @return: java.util.List<java.lang.Double>
    * @Author: shenll
    * @Date: 2021/5/16
    */
    private List<Double> caculatePowerLaw(int firstMaxDegree,int firstMinDegree,double firstGamma){
        int length=firstMaxDegree-firstMinDegree+1;
        List<Double> powerLaw=new ArrayList<>(length);
        double sum=0.0;
        for (int i = 0; i < length; i++) {
            double tmp=1/Math.pow(i+firstMinDegree,firstGamma);
            sum+=tmp;
            powerLaw.add(tmp);
        }
        for (int i = 0; i < length; i++) {
            powerLaw.set(i,powerLaw.get(i)/sum);
        }
        return powerLaw;
    }

    /**
    * @Description: 根据参数计算分布，并根据分布为每个节点初始化度数以及stubs
    * @Param: [firstMaxDegree, firstMinDegree, firstGamma.secondMaxDegree,secondMinDegree,secondGamma]
    * @return: void
    * @Author: shenll
    * @Date: 2021/5/16
    */
    protected void doAllocateDegree(int firstMaxDegree,int firstMinDegree,double firstGamma,
                                    int secondMaxDegree,int secondMinDegree,double secondGamma){
        List<Double> firstPowerlaw=caculatePowerLaw(firstMaxDegree,firstMinDegree,firstGamma);
        List<Double> secondPowerlaw=caculatePowerLaw(secondMaxDegree,secondMinDegree,secondGamma);
        Random r=new Random(random.nextInt());
        while (totalFirstLayerStubs==0 ||(totalFirstLayerStubs&1)!=0)//当总stubs为0或部位偶数时需要重新初始化
        {
            totalFirstLayerStubs=0;//重置总度数
//            totalFirstLayerDegree=0;

            for (Node n:nodeList){
                double cumProb=0.0;
                boolean flag=true;
                double test=r.nextDouble();
                for (int i = 0; i < firstPowerlaw.size(); i++) {
                    cumProb+=firstPowerlaw.get(i);
                    if (test-cumProb<=0.0000000001) {
//                        totalFirstLayerDegree+=i+firstMinDegree;
                        totalFirstLayerStubs+=i+firstMinDegree;
//                        n.setFirstDegree(i+firstMinDegree);
                        n.setFirstStubs(i+firstMinDegree);
                        flag=false;//成功设置将标志位设置为false
                        break;
                    }
                }
                if (flag) {//如果循环中未成功设置，则设置为最大度数
//                    n.setFirstDegree(firstMaxDegree);
                    n.setFirstStubs(firstMaxDegree);
                    totalFirstLayerStubs+=firstMaxDegree;
//                    totalFirstLayerDegree+=firstMaxDegree;
                }
            }
        }
        int sum=0;
        for (Node e :nodeList) sum+=e.getFirstStubs();
//        System.out.println(sum==totalFirstLayerStubs);
//        System.out.println("first layer degree allocated");
//        System.out.println("average degree:" +totalFirstLayerDegree/size);
        while (totalSecondLayerStubs==0 || (totalSecondLayerStubs&1)!=0){
            totalSecondLayerStubs=0;
//            totalSecondLayerDegree=0;
            for(Node n:nodeList){
                double cumProb=0.0;
                boolean flag=true;
                double test=r.nextDouble();
                for (int i = 0; i < secondPowerlaw.size(); i++) {
                    cumProb+=secondPowerlaw.get(i);
                    if (test<cumProb){
//                        totalSecondLayerDegree+=i+secondMinDegree;
                        totalSecondLayerStubs+=i+secondMinDegree;
//                        n.setSecondDegree(i+secondMinDegree);
                        n.setSecondStubs(i+secondMinDegree);
                        flag=false;

                        break;
                    }
                }
                if (flag){
//                    n.setSecondDegree(secondMaxDegree);
                    n.setSecondStubs(secondMaxDegree);
                    totalSecondLayerStubs+=secondMaxDegree;
//                    totalSecondLayerDegree+=secondMaxDegree;
                }
            }
        }
//        int s=0;
//        for (Node e :nodeList) s+=e.getSecondStubs();
//        System.out.println(s==totalSecondLayerStubs);
//        System.out.println("second layer degree allocated");
//        System.out.println("average degree:" +totalSecondLayerDegree/size);
    }

    /**
    * @Description: 初始化所有节点
    * @Param: []
    * @return: void
    * @Author: shenll
    * @Date: 2021/5/16
    */
    private void doInitAllNodes(){
        for (int i = 0; i < this.size; i++) {
            this.nodeList.add(new Node(i,NodeStatus.SUSPECT));
            withSecondStubsSet.add(i);
            withFirstStubsSet.add(i);
        }
    }

    /**
    * @Description: 根据参数初始化初始状态下处于感染态的节点
    * @Param: [nums]
    * @return: void
    * @Author: shenll
    * @Date: 2021/5/16
    */
    private void doInitInfectedNodes(int nums){
        Random r=new Random(random.nextInt());
        while (this.infectedSet.size()<nums){
            int tmp=r.nextInt(this.size);
            if (this.infectedSet.add(tmp)) this.nodeList.get(tmp).setStatus(NodeStatus.INFECTED);
        }
    }
    /**
     *
     */
    protected HashSet<Integer> infect(double beta, double lambda){
        HashSet<Integer> newInfectSet=new HashSet<>();
        Random r=new Random();
        for (int i:infectedSet){//对每一个感染节点
            Node n=nodeList.get(i);
            double totalInfect=0.0;
            for (int j:n.getFirstLayerNeighbor().keySet()){
                Node m=nodeList.get(j);
                 totalInfect+=Math.pow(m.getFirstStubs(),beta);
            }//计算总感染

            for (int j:n.getFirstLayerNeighbor().keySet()){
                Node m=nodeList.get(j);
                if (m.getStatus()==NodeStatus.INFECTED || m.getStatus()==NodeStatus.RECOVERED) continue;
                double prob;
//                if (Math.pow(m.getFirstDegree(),beta)>=totalInfect/n.getFirstLayerNeighbor().size())//?什么判断条件？
                prob=Math.pow(m.getFirstStubs(),beta)/totalInfect;
//                else prob=-1*Math.pow(m.getFirstDegree(),beta)/totalInfect;
                if (r.nextDouble()<lambda*(prob)) {
                    int tmp=m.getInfectedThreshold();
                    if (tmp<=1) {
                        m.setStatus(NodeStatus.INFECTED);
                        newInfectSet.add(m.getId());
                    }
                    else {
                        m.setInfectedThreshold(tmp-1);
                    }
                }
            }//感染
        }
        return newInfectSet;
    }

    protected HashSet<Integer> recover(double alpha ,double u0, double u1,double utilRate){
        HashSet<Integer> recoverSet=new HashSet<>();
        Random r=new Random();
        double div=Math.max(Math.pow(100,alpha),Math.pow(6,alpha));
        for (int i:infectedSet){
            //对每一个感染节点,计算获取的总资源数
            Node n=nodeList.get(i);

            double totalValue=0.0;
            for (int j:n.getSecondLayerNeighbor().keySet()){
                Node m=nodeList.get(j);
                if (m.getStatus()==NodeStatus.SUSPECT ){
                    double tmp=Math.pow(m.getSecondStubs(),alpha);
                    totalValue+=tmp/div;

                }
            }
            double prob=1-Math.pow((1-u0),utilRate*totalValue)+u1;
            if (r.nextDouble()<prob) {
                n.setStatus(NodeStatus.RECOVERED);
                recoverSet.add(i);
                recoverNodes+=1;
            }
        }
        return recoverSet;
    }

    public void update(double u0,double u1,double utilRate,double lambda,double alpha,double beta){
        HashSet<Integer> newInfect=infect(beta,lambda);//感染在第一层传播
        HashSet<Integer> newRecover=recover(alpha,u0,u1,utilRate);
        System.out.println("new infect nums: "+newInfect.size());
        for (int i:newRecover) infectedSet.remove(i);
        infectedSet.addAll(newInfect);
        System.out.println("recover nums: "+recoverNodes);
    }

    public void setRecoverNodes(int recoverNodes) {
        this.recoverNodes = recoverNodes;
    }

    public Set<Integer> getInfectedSet() {
        return infectedSet;
    }

    public void setInfectedSet(){
        this.infectedSet=new HashSet<>();
        doInitInfectedNodes(5);
    }


    public int getSize() {
        return size;
    }

    public int getRecoverNodes() {
        return recoverNodes;
    }

    public List<Node> getNodeList() {
        return nodeList;
    }
}
