// CIS 121, QuadTree

public class QuadTreeNodeImpl implements QuadTreeNode {
    private int length;
    private int x;
    private int y;
    private int color;
    private QuadTreeNodeImpl[] children;
    private boolean hasColor;
    
    
    QuadTreeNodeImpl(int length, int x, int y, int color, boolean hasColor) {
        this.length = length;
        this.x = x;
        this.y = y;
        this.color = color;
        children = new QuadTreeNodeImpl[4];
        children[0] = null;
        children[1] = null;
        children[2] = null;
        children[3] = null;
        this.hasColor = hasColor;
    }
    
    /**
     * ! Do not delete this method !
     * Please implement your logic inside this method without modifying the signature
     * of this method, or else your code won't compile.
     * <p/>
     * As always, if you want to create another method, make sure it is not public.
     *
     * @param image image to put into the tree
     * @return the newly build QuadTreeNode instance which stores the compressed image
     * @throws IllegalArgumentException if image is null
     * @throws IllegalArgumentException if image is empty
     * @throws IllegalArgumentException if image.length is not a power of 2
     * @throws IllegalArgumentException if image, the 2d-array, is not a perfect square
     */
    public static QuadTreeNodeImpl buildFromIntArray(int[][] image) {
        // null image 
        if (image == null) {
            throw new IllegalArgumentException("null image");
        }
        // 0 length image
        if (image.length == 0) {
            throw new IllegalArgumentException("empty image");
        }
        // not power of 2 length image
        if (Math.log(image.length) / Math.log(2) % 1 != 0) {
            throw new IllegalArgumentException("not a power of 2 image");
        }
        // not a square image
        for (int i = 0; i < image.length; i++) {
            if (image.length != image[i].length) {
                throw new IllegalArgumentException("not a square");
            }
        }
        return compress(image, image.length, 0, 0);
    }
    
    static QuadTreeNodeImpl compress(int[][] image, int length, int x, int y) {
        if (length == 1) {
            return new QuadTreeNodeImpl(1, x, y, image[y][x], true);
        }
        QuadTreeNodeImpl topLeft = compress(image, length / 2, x, y);
        QuadTreeNodeImpl topRight = compress(image, length / 2, x + length / 2, y);
        QuadTreeNodeImpl bottomLeft = compress(image, length / 2, x, y + length / 2);
        QuadTreeNodeImpl bottomRight = compress(image, length / 2, x + length / 2, y + length / 2);
        
        if (!topLeft.hasColor || !topRight.hasColor || !bottomLeft.hasColor || 
                !bottomRight.hasColor || topLeft.color != topRight.color ||
                topRight.color != bottomLeft.color || 
                bottomLeft.color != bottomRight.color) {
            QuadTreeNodeImpl children = new QuadTreeNodeImpl(length, x, y, 0, false);
            
            children.setChildren(0, topLeft);
            children.setChildren(1, topRight);
            children.setChildren(2, bottomLeft);
            children.setChildren(3, bottomRight);
            return children;          
        } else {
            return new QuadTreeNodeImpl(length, x, y, image[y][x], true);
        }
    }

    @Override
    public int getColor(int x, int y) {
        if (x >= this.length || y >= this.length || x < 0 || y < 0) {
            throw new IllegalArgumentException("invalid bounds");
        }
        if (!isLeaf()) {
            if (x < this.length / 2 && y < this.length / 2) {
                return this.children[0].getColor(x, y);
            }
            if (x >= this.length / 2 && y < this.length / 2) {
                return this.children[1].getColor(x - this.length / 2, y);
            }
            if (x < this.length / 2 && y >= this.length / 2) {
                return this.children[2].getColor(x, y - length / 2);
            }
            if (x >= this.length / 2 && y >= this.length / 2) {
                return this.children[3].getColor(x - this.length / 2, y - this.length / 2);
            }
        }
        return this.color;
    }

    @Override
    public void setColor(int x, int y, int c) {
        if (x >= this.length || y >= this.length || x < 0 || y < 0) {
            throw new IllegalArgumentException("invalid bounds");
        }
        if (this.length == 1) {
            this.color = c;
            return;
        }
        if (this.isLeaf() && this.length != 1) {
            this.hasColor = false;
            
            QuadTreeNodeImpl topLeft = new QuadTreeNodeImpl(this.length / 2, 
                    this.x, this.y, this.color, true);
            QuadTreeNodeImpl topRight = new QuadTreeNodeImpl(this.length / 2, 
                    this.x + this.length / 2, this.y, this.color, true);
            QuadTreeNodeImpl bottomLeft = new QuadTreeNodeImpl(this.length / 2, 
                    this.x, this.y + this.length / 2, this.color, true);
            QuadTreeNodeImpl bottomRight = new QuadTreeNodeImpl(this.length / 2,
                    this.x + this.length / 2, this.y + this.length / 2, this.color, true);
            
            this.setChildren(0, topLeft);
            this.setChildren(1, topRight);
            this.setChildren(2, bottomLeft);
            this.setChildren(3, bottomRight);
        }
        if (x < this.length / 2 && y < this.length / 2) {
            this.children[0].setColor(x, y, c);
        }
        if (x >= this.length / 2 && y < this.length / 2) {
            this.children[1].setColor(x - this.length / 2, y, c);
        }
        if (x < this.length / 2 && y >= this.length / 2) {
            this.children[2].setColor(x, y - this.length / 2, c);
        }
        if (x >= this.length / 2 && y >= this.length / 2) {
            this.children[3].setColor(x - this.length / 2, y - this.length / 2, c);
        }
        if (!this.isLeaf()) {
            this.optimalQuadTreeCheck();
        }      
    }
    
    
    void optimalQuadTreeCheck() {
        // children to leaf
        if (this.getChildren()[0].getColor(0,0) == this.getChildren()[1].getColor(0,0) && 
                this.getChildren()[1].getColor(0,0) == this.getChildren()[2].getColor(0,0) &&
                this.getChildren()[2].getColor(0,0) == this.getChildren()[3].getColor(0,0) && 
                !this.isLeaf()) {
            this.color = this.getChildren()[0].color;
            this.hasColor = true;
            this.setChildren(0, null);
            this.setChildren(1, null);
            this.setChildren(2, null);
            this.setChildren(3, null);
        }
    }

    @Override
    public QuadTreeNode getQuadrant(QuadName quadrant) {
        if (quadrant.equals(QuadTreeNode.QuadName.TOP_LEFT)) {
            return this.getChildren()[0];
        } else if (quadrant.equals(QuadTreeNode.QuadName.TOP_RIGHT)) {
            return this.getChildren()[1];
        } else if (quadrant.equals(QuadTreeNode.QuadName.BOTTOM_LEFT)) {
            return this.getChildren()[2];
        } else if (quadrant.equals(QuadTreeNode.QuadName.BOTTOM_RIGHT)) {
            return this.getChildren()[3];
        }
        return null;
    }

    @Override
    public int getDimension() {
        return this.length;
    }

    @Override
    public int getSize() {
        if (this.isLeaf()) {
            return 1;
        }
        return 1 + children[0].getSize() + children[1].getSize() + 
                children[2].getSize() + children[3].getSize();
    }

    @Override
    public boolean isLeaf() {
        return this.hasColor;
    }

    @Override
    public int[][] decompress() {
        int[][] original = new int[this.length][this.length];
        if (this.isLeaf()) {
            for (int i = 0; i < original.length; i++) {
                for (int j = 0; j < original[0].length; j++) {
                    original[i][j] = this.color;
                }
            }
        } else {
            decompressHelper(this, original);
        }
        return original;
    }
    
    void decompressHelper(QuadTreeNodeImpl node, int[][] arr) {
        if (node != null) {
            if (node.length == 1) {
                arr[node.y][node.x] = node.color;
                return;
        
            }
            if (node.isLeaf() && node.length > 1) {
                for (int i = node.x; i < node.x + node.length; i++) {
                    for (int j = node.y; j < node.y + node.length; j++) {
                        arr[j][i] = node.getColor(i - node.x, j - node.y);
                    }
                }
            } 
            if (!node.isLeaf()) { 
                decompressHelper(node.children[0], arr);
                decompressHelper(node.children[1], arr);
                decompressHelper(node.children[2], arr);
                decompressHelper(node.children[3], arr);
            }
        }
    }

    @Override
    public double getCompressionRatio() {
        return (double) this.getSize() / ((double) this.getDimension() 
                * (double) this.getDimension());
    }
    
    QuadTreeNodeImpl[] getChildren() {
        QuadTreeNodeImpl[] childrenR = new QuadTreeNodeImpl[4];
        for (int i = 0; i < children.length; i++) {
            childrenR[i] = this.children[i];
        }
        return childrenR;
    }
    
    void setChildren(int x, QuadTreeNodeImpl node) {
        this.children[x] = node;
    }
    
    
}
