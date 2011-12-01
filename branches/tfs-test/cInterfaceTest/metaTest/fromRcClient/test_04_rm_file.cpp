#include"tfs_client_impl_init.h"


TEST_F(TfsInit,test_01_rmFile_right_filePath)
{
   int ret;

   ret=tfsclient->create_file(uid,"/metarcgtestrmFile1");
   EXPECT_EQ(0,ret);
   ret=tfsclient->rm_file(uid,"/metarcgtestrmFile1");
   EXPECT_EQ(0,ret);
}

TEST_F(TfsInit,test_02_rmFile_double_times)
{
   int ret;

   ret=tfsclient->create_file(uid,"/metarcgtestrmFile2");
   EXPECT_EQ(0,ret);
   ret=tfsclient->rm_file(uid,"/metarcgtestrmFile2");
   EXPECT_EQ(0,ret);
   ret=tfsclient->rm_file(uid,"/metarcgtestrmFile2");
   EXPECT_GT(0,ret);
}

TEST_F(TfsInit,test_03_rmFile_not_exist)
{
   int ret;

   ret=tfsclient->rm_file(uid,"/metarcgtestrmFile3");
   EXPECT_GT(0,ret);
}

TEST_F(TfsInit,test_04_rmFile_null_filePath)
{
   int ret;

   ret=tfsclient->rm_file(uid,NULL);
   EXPECT_GT(0,ret);
}

TEST_F(TfsInit,test_05_rmFile_empty_filePath)
{
   int ret;
   char name[1]="";
   ret=tfsclient->rm_file(uid,name);
   EXPECT_GT(0,ret);
}

TEST_F(TfsInit,test_06_rmFile_wrong_filePath_1)
{
   int ret;

   ret=tfsclient->create_file(uid,"/metarcgtestrmFile6");
   EXPECT_EQ(0,ret);
   ret=tfsclient->rm_file(uid,"metarcgtestrmFile6");
   EXPECT_GT(0,ret);
   ret=tfsclient->rm_file(uid,"/metarcgtestrmFile6");
   EXPECT_EQ(0,ret);
}

TEST_F(TfsInit,test_07_rmFile_wrong_filePath_2)
{
   int ret;

   ret=tfsclient->create_file(uid,"/metarcgtestrmFile7");
   EXPECT_EQ(0,ret);
   ret=tfsclient->rm_file(uid,"////metarcgtestrmFile7/////");
   EXPECT_EQ(0,ret);
}

TEST_F(TfsInit,test_08_rmFile_wrong_filePath_3)
{
   int ret;

   ret=tfsclient->rm_file(uid,"/");
   EXPECT_GT(0,ret);
}



int main(int argc,char**argv)
{
    testing::InitGoogleTest(&argc,argv);
    return RUN_ALL_TESTS();
}
