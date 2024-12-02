package com.example.aethoneventsapp;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.*;
import com.google.android.gms.tasks.Task;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;

import org.testng.annotations.BeforeMethod;

import static org.mockito.Mockito.*;

import android.os.Looper;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(JUnit4.class)
public class ExampleUnitTest {

    private FirebaseFirestore mockFirestore;

    private CollectionReference mockCollectionReference;

    Query mockQuery;

    Task<QuerySnapshot> mockTask;

    QuerySnapshot mockQuerySnapshot;

    QueryDocumentSnapshot mockDocumentSnapshot;

    OnCompleteListener<QuerySnapshot> mockOnCompleteListener;

    @Mock
    private LandingActivity landingActivity;


    @Ignore
    public void setUp() throws Exception{
        //landingActivity = Robolectric.buildActivity(LandingActivity.class).create().get();
        landingActivity = Mockito.spy(landingActivity);
        mockFirestore = Mockito.mock(FirebaseFirestore.class);
        mockCollectionReference = Mockito.mock(CollectionReference.class);
        mockQuery = Mockito.mock(Query.class);
        mockTask = Mockito.mock(Task.class);
        mockQuerySnapshot = Mockito.mock(QuerySnapshot.class);
        mockDocumentSnapshot = Mockito.mock(QueryDocumentSnapshot.class);

    }


    @Ignore
    public void testSignUpNavigation(){
        // Simulating Firebase response that calls the method navigateToProfileSetup

        when(mockFirestore.collection("users")).thenReturn(mockCollectionReference);
        when(mockCollectionReference.whereEqualTo(eq("deviceId"), any())).thenReturn(mockQuery);

        when(mockQuery.get()).thenReturn(mockTask);

        // Mock task behavior to simulate no matching user found
        when(mockTask.isSuccessful()).thenReturn(true);
        when(mockTask.getResult()).thenReturn(mockQuerySnapshot);
        when(mockQuerySnapshot.isEmpty()).thenReturn(true);

        // Spy or mock navigateToProfileSetup to verify it's called
        doNothing().when(landingActivity).navigateToProfileSetup();

        // Call the method being tested
        landingActivity.checkDeviceRecognition(mockFirestore);

        // Verify that navigateToProfileSetup is called
        verify(landingActivity).navigateToProfileSetup();
        verify(landingActivity).navigateToProfileSetup();



    }
    @Ignore
    public void NoTest(){

    }

}

//        when(mockTask.getResult()).thenReturn(mockQuerySnapshot);
//        when(mockQuerySnapshot.isEmpty()).thenReturn(false);
//        when(mockQuerySnapshot.getDocuments()).thenReturn(Collections.singletonList(mockDocumentSnapshot));
//        when(mockDocumentSnapshot.getBoolean("isAdmin")).thenReturn(false); // User is not an admin

