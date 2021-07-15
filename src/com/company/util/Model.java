package com.company.util;

import java.io.*;
import java.util.HashSet;

public class Model  {
    private Network network;
    private String modelPath;


    public Model(String modelPath,int firstMaxDegree,int firstMinDegree,double firstGamma,
                 int secondMaxDegree,int secondMinDegree,double secondGamma, double threhold) throws Exception
    {
        this.modelPath = modelPath;
        this.network=new Network(5000,firstMaxDegree,firstMinDegree,firstGamma,5,secondMaxDegree,secondMinDegree,secondGamma,threhold);
        File file=new File(modelPath);
        FileOutputStream fileOutputStream=new FileOutputStream(file);
        ObjectOutputStream outputStream=new ObjectOutputStream(fileOutputStream);
        outputStream.writeObject(network);
        outputStream.flush();
        fileOutputStream.close();
        outputStream.close();
    }

    public Model(String modelPath) {
        this.modelPath = modelPath;

    }

    public void train() throws IOException, ClassNotFoundException {
        int trainRounds=1000;
        String saveFile="fg1__alpha_n05_beta_n05_f_005.txt";
        BufferedWriter bufferedWriter=new BufferedWriter(new FileWriter(saveFile));
        bufferedWriter.append("");
        bufferedWriter.newLine();
        File file=new File(modelPath);
        FileInputStream fileInputStream=new FileInputStream(file);
        ObjectInputStream objectInputStream=new ObjectInputStream(fileInputStream);
        Network trainNetwork = (Network) objectInputStream.readObject();
        objectInputStream.close();
        fileInputStream.close();
        for (int k = 5; k < 6; k++) {
            bufferedWriter.append("nodes nums: 10000"+"\t"+"infectThreshold:"+k/100.0+"\t"+"alpha:"+-0.5+"\t"+"beta:"+-0.5);
            bufferedWriter.newLine();
            bufferedWriter.append("Steps"+"\t"+"InfectRate"+"\t"+"R"+"\t"+"R^2");
            bufferedWriter.newLine();
                    for (int i = 0; i < trainRounds; i++) {
                        for (int j = 0; j < 100; j++) {
                            System.out.println("InfectRate:"+j+" Steps:"+i);
                            trainNetwork.reset();
                            trainNetwork.setAllInfectThreshold(k/100.0);
                            int times=0;
                            while (trainNetwork.getInfectedSet().size()!=0 && times<9999){
                                trainNetwork.update(0.1,0.1,0.1,j/100.0,-0.5,-0.5);
                                times++;
                                System.out.println("infected left :"+trainNetwork.getInfectedSet().size());
                            }
                            bufferedWriter.append(i+"\t"+j/100.0+"\t"+trainNetwork.getRecoverNodes()+"\t"+Math.pow(trainNetwork.getRecoverNodes(),2));
                            bufferedWriter.newLine();
                        }
                        bufferedWriter.flush();
                }
            }
            bufferedWriter.close();
        }




    public void setNetwork(Network network) {
        this.network = network;
    }

    public void setModelPath(String modelPath) {
        this.modelPath = modelPath;
    }

    public Network getNetwork() {
        return network;
    }

    public String getModelPath() {
        return modelPath;
    }
}
