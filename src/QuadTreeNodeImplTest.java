import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class QuadTreeNodeImplTest {

    private int[][] small;
    private int[][] single;
    private int[][] mongoBongo;
    QuadTreeNodeImpl node;
    QuadTreeNodeImpl leaf;
    
    @Before
    public void setUp() {
        small = new int[][]{
            {1, 0},
            {1, 0}
        };        
        
        single = new int[][] {
            {1}
        };
        
        mongoBongo = new int[][]{
            {1, 1, 1, 1, 1, 1, 1, 0},
            {1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1},
            {0, 0, 1, 0, 0, 0, 0, 0},
            {0, 0, 1, 1, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 1, 1},
            {0, 0, 0, 0, 0, 0, 1, 1}
        };
        
        node = new QuadTreeNodeImpl(2, 0, 0, 0, false);
        node.setChildren(0, new QuadTreeNodeImpl(1, 0, 0, 1, true));
        node.setChildren(1, new QuadTreeNodeImpl(1, 1, 0, 0, true));
        node.setChildren(2, new QuadTreeNodeImpl(1, 0, 1, 1, true));
        node.setChildren(3, new QuadTreeNodeImpl(1, 1, 1, 0, true));
        
        leaf = new QuadTreeNodeImpl(2, 0, 0, 0, true);
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void nullImageTest() {
        QuadTreeNodeImpl node = QuadTreeNodeImpl.buildFromIntArray(null);
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void zeroLengthImageTest() {
        QuadTreeNodeImpl node = QuadTreeNodeImpl.buildFromIntArray(new int[][] {});
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void notPowerOfTwoImageTest() {
        QuadTreeNodeImpl node = QuadTreeNodeImpl.buildFromIntArray(new int[6][6]);
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void notASquareImageTest() {
        QuadTreeNodeImpl node = QuadTreeNodeImpl.buildFromIntArray(new int[8][6]);
    }
    
    @Test
    public void buildFromIntArraySmallTest() {
        QuadTreeNodeImpl node = QuadTreeNodeImpl.buildFromIntArray(small);
        assertArrayEquals(small, node.decompress());
    }
    
    @Test
    public void getSizeChildrenTest() {
        assertEquals(5, node.getSize());
    }
    
    @Test 
    public void getSizeLeafTest() {
        assertEquals(1, leaf.getSize());
    }
    
    @Test
    public void getDimensionTest() {
        assertEquals(2, node.getDimension());
        assertEquals(1, node.getChildren()[0].getDimension());
        assertEquals(1, node.getChildren()[1].getDimension());
        assertEquals(1, node.getChildren()[2].getDimension());
        assertEquals(1, node.getChildren()[3].getDimension());
        assertEquals(2, leaf.getDimension());
    }
    
    @Test
    public void getCompressionRatioTest() {
        assertEquals(5.0 / 4.0, node.getCompressionRatio(), 0.01);
    }
    
    @Test
    public void decompressTest() {
        assertArrayEquals(small, QuadTreeNodeImpl.buildFromIntArray(small).decompress());
    }
    
    @Test
    public void decompressLargeTest() {
        assertArrayEquals(mongoBongo, QuadTreeNodeImpl.buildFromIntArray(mongoBongo).decompress());
    }
    
    @Test
    public void getColorTest() {
        assertEquals(1, node.getColor(0, 0));
        assertEquals(0, node.getColor(1, 0));
        assertEquals(1, node.getColor(0, 1));
        assertEquals(0, node.getColor(1, 1));
        assertEquals(0, leaf.getColor(0, 0));
        assertEquals(0, leaf.getColor(1, 0));
        assertEquals(0, leaf.getColor(0, 1));
        assertEquals(0, leaf.getColor(1, 1));
    }
    
    @Test
    public void setColorTest() {
        QuadTreeNodeImpl node2 = QuadTreeNodeImpl.buildFromIntArray(small);
        node2.setColor(0, 0, 100);
        assertEquals(100, node2.getColor(0, 0));
        node2.setColor(0, 1, 100);
        assertEquals(100, node2.getColor(0, 1));
        node2.setColor(1, 0, 100);
        assertEquals(100, node2.getColor(1, 0));
       
        node2.setColor(1, 1, 100);
        assertEquals(100, node2.getColor(1, 1));
        int[][] result = new int[][] {
            {100, 100},
            {100, 100}
        };
        assertArrayEquals(result, node2.decompress());
    }
    
    @Test
    public void setColorBreakTest() {
        QuadTreeNodeImpl mongo = QuadTreeNodeImpl.buildFromIntArray(mongoBongo);
        mongo.setColor(7, 7, 0);
        int[][] result = new int[][]{
            {1, 1, 1, 1, 1, 1, 1, 0},
            {1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1},
            {0, 0, 1, 0, 0, 0, 0, 0},
            {0, 0, 1, 1, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 1, 1},
            {0, 0, 0, 0, 0, 0, 1, 0}
        };
        assertArrayEquals(result, mongo.decompress());
    }
    
    @Test
    public void setColorDoubleBreakTest() {
        QuadTreeNodeImpl mongo = QuadTreeNodeImpl.buildFromIntArray(mongoBongo);
        mongo.setColor(0, 0, 0);
        assertEquals(0, mongo.getColor(0, 0));
        int[][] result = new int[][]{
            {0, 1, 1, 1, 1, 1, 1, 0},
            {1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1},
            {0, 0, 1, 0, 0, 0, 0, 0},
            {0, 0, 1, 1, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 1, 1},
            {0, 0, 0, 0, 0, 0, 1, 1}
        };
        
        assertArrayEquals(result, mongo.decompress());
    }
    
    @Test
    public void setColorFuseTest() {
        QuadTreeNodeImpl mongo = QuadTreeNodeImpl.buildFromIntArray(mongoBongo);
        mongo.setColor(3, 4, 1);
        assertEquals(1, mongo.getColor(3, 4));
        int[][] result = new int[][]{
            {1, 1, 1, 1, 1, 1, 1, 0},
            {1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1},
            {0, 0, 1, 1, 0, 0, 0, 0},
            {0, 0, 1, 1, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 1, 1},
            {0, 0, 0, 0, 0, 0, 1, 1}
        };
        assertTrue(mongo.getChildren()[3].getChildren()[3].isLeaf());
        assertArrayEquals(result, mongo.decompress());
    }
    
    @Test 
    public void setColorDoubleFuseTest() {
        QuadTreeNodeImpl mongo = QuadTreeNodeImpl.buildFromIntArray(mongoBongo);
        mongo.setColor(7, 0, 1);
        assertEquals(1, mongo.getColor(7, 0));
        int[][] result = new int[][]{
            {1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1},
            {0, 0, 1, 0, 0, 0, 0, 0},
            {0, 0, 1, 1, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 1, 1},
            {0, 0, 0, 0, 0, 0, 1, 1}
        };
        assertTrue(mongo.getChildren()[1].isLeaf());
        assertArrayEquals(result, mongo.decompress());
    }
}
